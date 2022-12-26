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

package tasostilsi.uom.edu.gr.metricsCalculator.Models.DTOs;

import java.util.Objects;

public class ProjectDTO {
	
	private String url;
	private String owner;
	private String repo;
	
	public ProjectDTO() { }
	
	public ProjectDTO(String url) {
		this.url = url;
		this.owner = getRepositoryOwner();
		this.repo = getRepositoryName();
	}
	
	public ProjectDTO(String url, String owner, String repo) {
		this.url = url;
		this.owner = owner;
		this.repo = repo;
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
		ProjectDTO project = (ProjectDTO) o;
		return Objects.equals(url, project.url) && Objects.equals(owner, project.owner) && Objects.equals(repo, project.repo);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(url, owner, repo);
	}
}
