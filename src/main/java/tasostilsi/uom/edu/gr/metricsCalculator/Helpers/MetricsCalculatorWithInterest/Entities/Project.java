/*
 * ******************************************************************************
 *  * Copyright (C) 2022-2023 University of Macedonia
 *  *
 *  * This program and the accompanying materials are made
 *  * available under the terms of the Eclipse Public License 2.0
 *  * which is available at https://www.eclipse.org/legal/epl-2.0/
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *****************************************************************************
 */

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tasostilsi.uom.edu.gr.metricsCalculator.Helpers.Enums.State;

import javax.persistence.*;
import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "project")
@Transactional(isolation= Isolation.SERIALIZABLE)
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "url", nullable = false, unique = true)
	private String url;
	@Column(name = "owner", nullable = false)
	private String owner;
	@Column(name = "repo", nullable = false)
	private String repo;
	@Column(name = "path", nullable = false)
	private String clonePath;
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
//	@JsonIgnore
	private Set<CalculatedJavaFile> javaFiles;
	@Column(name = "state", nullable = false)
	private String state;
	
	public Project(String url, String clonePath) {
		this.url = url;
		this.owner = getRepositoryOwner();
		this.repo = getRepositoryName();
		this.clonePath = clonePath;
		this.javaFiles = new HashSet<>();
		this.state = State.INITIAL.name();
	}
	
	public Project(String url) {
		this.url = url;
		this.owner = getRepositoryOwner();
		this.repo = getRepositoryName();
		this.clonePath = File.separatorChar + "tmp" + File.separatorChar + getRepositoryOwner() + File.separatorChar + getRepositoryName();
		this.javaFiles = new HashSet<>();
		this.state = State.INITIAL.name();
	}
	
	public Project(String url, String owner, String repo, String clonePath) {
		this.url = url;
		this.owner = owner;
		this.repo = repo;
		this.clonePath = clonePath;
		this.javaFiles = new HashSet<>();
		this.state = State.INITIAL.name();
	}
	
	public Project(String clonePath, Set<CalculatedJavaFile> javaFiles) {
		this.clonePath = clonePath;
		this.javaFiles = javaFiles;
		this.state = State.INITIAL.name();
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getRepo() {
		return repo;
	}
	
	public void setRepo(String repo) {
		this.repo = repo;
	}
	
	public String getClonePath() {
		return clonePath;
	}
	
	public void setClonePath(String clonePath) {
		this.clonePath = clonePath;
	}
	
	private String getRepositoryOwner() {
		String newURL = preprocessURL();
		String[] urlSplit = newURL.split("/");
		return urlSplit[urlSplit.length - 2].replaceAll(".*@.*:", "");
	}
	
	private String getRepositoryName() {
		String newURL = preprocessURL();
		String[] urlSplit = newURL.split("/");
		return urlSplit[urlSplit.length - 1];
	}
	
	private String preprocessURL() {
		String newURL = this.getUrl();
		if (newURL.endsWith(".git/"))
			newURL = newURL.replace(".git/", "");
		if (newURL.endsWith(".git"))
			newURL = newURL.replace(".git", "");
		if (newURL.endsWith("/"))
			newURL = newURL.substring(0, newURL.length() - 1);
		return newURL;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Project project = (Project) o;
		return Objects.equals(url, project.url) && Objects.equals(owner, project.owner) && Objects.equals(repo, project.repo) && Objects.equals(clonePath, project.clonePath);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(url, owner, repo, clonePath);
	}
	
	public Set<CalculatedJavaFile> getJavaFiles() {
		return javaFiles;
	}
	
	public void setJavaFiles(Set<CalculatedJavaFile> javaFiles) {
		this.javaFiles = javaFiles;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
}
