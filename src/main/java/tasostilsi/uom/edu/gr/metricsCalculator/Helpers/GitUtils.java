package tasostilsi.uom.edu.gr.metricsCalculator.Helpers;

import ch.qos.logback.classic.Logger;
import data.Globals;
import db.InsertToDB;
import infrastructure.Project;
import infrastructure.Revision;
import infrastructure.newcode.DiffEntry;
import infrastructure.newcode.PrincipalResponseEntity;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.LoggerFactory;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.ProjectService;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class GitUtils {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GitUtils.class);

    private static GitUtils instance;

    private GitUtils() {
    }

    public static GitUtils getInstance() {
        if (instance == null) {
            instance = new GitUtils();
        }
        return instance;
    }

    /**
     * Inserts the data of the first revision (in list).
     *
     * @param project         the project we are referring to
     * @param currentRevision the current revision we are analysing
     */
    public void insertData(Project project, Revision currentRevision) {
        if (Globals.getJavaFiles().isEmpty())
            InsertToDB.insertEmpty(project, currentRevision);
        else {
            Globals.getJavaFiles().forEach(jf -> InsertToDB.insertFileToDatabase(project, jf, currentRevision));
            Globals.getJavaFiles().forEach(jf -> InsertToDB.insertMetricsToDatabase(project, jf, currentRevision));
        }
    }

    /**
     * Performs a set subtraction between the received commits and the existing ones (in database).
     *
     * @param receivedCommitIds the list containing the received commits
     * @param existingCommitIds the list containing the existing commits
     * @return the result of the subtraction
     */
    public List<String> findDifferenceInCommitIds(List<String> receivedCommitIds, List<String> existingCommitIds) {
        List<String> diffCommitIds = new ArrayList<>(receivedCommitIds);
        if (Objects.nonNull(existingCommitIds))
            diffCommitIds.removeAll(existingCommitIds);
        return diffCommitIds;
    }

    /**
     * Gets all commit ids for a specific git repo.
     *
     * @param git the git object
     */
    public List<String> getCommitIds(Git git) {
        List<String> commitIds = new ArrayList<>();
        try {
            String treeName = getHeadName(git.getRepository());
            for (RevCommit commit : git.log().add(git.getRepository().resolve(treeName)).call())
                commitIds.add(commit.getName());
        } catch (Exception ignored) {
        }
        return commitIds;
    }

    private String getHeadName(Repository repo) {
        String result = null;
        try {
            ObjectId id = repo.resolve(Constants.HEAD);
            result = id.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Gets all commit ids for a specific git repo.
     *
     * @param git the git object
     */
    public PrincipalResponseEntity[] getResponseEntitiesAtCommit(Git git, String sha) {
        RevCommit headCommit;
        try {
            headCommit = git.getRepository().parseCommit(ObjectId.fromString(sha));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        RevCommit diffWith = Objects.requireNonNull(headCommit).getParent(0);
        FileOutputStream stdout = new FileOutputStream(FileDescriptor.out);
        PrincipalResponseEntity[] principalResponseEntities = new PrincipalResponseEntity[1];
        try (DiffFormatter diffFormatter = new DiffFormatter(stdout)) {
            diffFormatter.setRepository(git.getRepository());
            try {
                Set<DiffEntry> addDiffEntries = new HashSet<>();
                Set<DiffEntry> modifyDiffEntries = new HashSet<>();
                Set<DiffEntry> renameDiffEntries = new HashSet<>();
                Set<DiffEntry> deleteDiffEntries = new HashSet<>();
                RenameDetector renameDetector = new RenameDetector(git.getRepository());
                renameDetector.addAll(diffFormatter.scan(diffWith, headCommit));
                for (org.eclipse.jgit.diff.DiffEntry entry : renameDetector.compute()) {
                    if ((entry.getChangeType().equals(org.eclipse.jgit.diff.DiffEntry.ChangeType.ADD) || entry.getChangeType().equals(org.eclipse.jgit.diff.DiffEntry.ChangeType.COPY)) && entry.getNewPath().toLowerCase().endsWith(".java"))
                        addDiffEntries.add(new DiffEntry(entry.getOldPath(), entry.getNewPath(), entry.getChangeType().toString()));
                    else if (entry.getChangeType().equals(org.eclipse.jgit.diff.DiffEntry.ChangeType.MODIFY) && entry.getNewPath().toLowerCase().endsWith(".java"))
                        modifyDiffEntries.add(new DiffEntry(entry.getOldPath(), entry.getNewPath(), entry.getChangeType().toString()));
                    else if (entry.getChangeType().equals(org.eclipse.jgit.diff.DiffEntry.ChangeType.DELETE) && entry.getOldPath().toLowerCase().endsWith(".java"))
                        deleteDiffEntries.add(new DiffEntry(entry.getOldPath(), entry.getNewPath(), entry.getChangeType().toString()));
                    else if (entry.getChangeType().equals(org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME) && entry.getNewPath().toLowerCase().endsWith(".java")) {
                        renameDiffEntries.add(new DiffEntry(entry.getOldPath(), entry.getNewPath(), entry.getChangeType().toString()));
                    }
                }
                principalResponseEntities[0] = new PrincipalResponseEntity(headCommit.getName(), headCommit.getCommitTime(), addDiffEntries, modifyDiffEntries, renameDiffEntries, deleteDiffEntries);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return principalResponseEntities;
    }

    /**
     * Clones a repo to a specified path.
     *
     * @param project the project we are referring to
     * @return a git object
     */
    public Git cloneRepository(Project project, String accessToken) {

        try {
            Utils.getInstance().deleteSourceCode(new File(project.getClonePath()));
        } catch (Exception ignored) {
            LOGGER.warn(ignored.toString());
        }

        try {
            if (Objects.isNull(accessToken))
                return Git.cloneRepository()
                        .setURI(project.getUrl())
                        .setDirectory(new File(project.getClonePath()))
                        .call();
            else {
                return Git.cloneRepository()
                        .setURI(project.getUrl())
                        .setDirectory(new File(project.getClonePath()))
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, ""))
                        .call();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checkouts to specified commitId (SHA)
     *
     * @param project         the project we are referring to
     * @param currentRevision the revision we are checking out to
     * @param git             a git object
     */
    public void checkout(Project project, String accessToken, Revision currentRevision, Git git) throws GitAPIException {
        try {
            git.checkout().setCreateBranch(true).setName("version" + currentRevision.getRevisionCount()).setStartPoint(currentRevision.getSha()).call();
        } catch (CheckoutConflictException e) {
            Utils.getInstance().deleteSourceCode(new File(project.getClonePath()));
            cloneRepository(project, accessToken);
            git.checkout().setCreateBranch(true).setName("version" + currentRevision.getRevisionCount()).setStartPoint(currentRevision.getSha()).call();
        }
    }

}
