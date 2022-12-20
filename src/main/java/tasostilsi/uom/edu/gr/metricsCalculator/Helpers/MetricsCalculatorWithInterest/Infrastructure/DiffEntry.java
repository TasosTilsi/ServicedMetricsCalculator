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

package tasostilsi.uom.edu.gr.metricsCalculator.Helpers.MetricsCalculatorWithInterest.Infrastructure;

import java.util.Objects;

public class DiffEntry {
	
	private String oldFilePath;
	private String newFilePath;
	private String changeType;
	
	public DiffEntry(String oldFilePath, String newFilePath, String changeType) {
		this.oldFilePath = oldFilePath;
		this.newFilePath = newFilePath;
		this.changeType = changeType;
	}
	
	public String getOldFilePath() {
		return oldFilePath;
	}
	
	public void setOldFilePath(String oldFilePath) {
		this.oldFilePath = oldFilePath;
	}
	
	public String getNewFilePath() {
		return newFilePath;
	}
	
	public void setNewFilePathString(String newFilePath) {
		this.newFilePath = newFilePath;
	}
	
	public String getChangeType() {
		return changeType;
	}
	
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DiffEntry diffEntry = (DiffEntry) o;
		return Objects.equals(oldFilePath, diffEntry.oldFilePath) && Objects.equals(newFilePath, diffEntry.newFilePath) && Objects.equals(changeType, diffEntry.changeType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(oldFilePath, newFilePath, changeType);
	}
}
