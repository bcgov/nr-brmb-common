package ca.bc.gov.brmb.common.persistence.code.dto;

import java.time.Instant;

public abstract class AuditDto implements BaseDto {

	private static final long serialVersionUID = 1L;

	protected Long revisionCount;
	protected String createUser;
	protected Instant createTimestamp;
	protected String updateUser;
	protected Instant updateTimestamp;

	public Long getRevisionCount() {
		return revisionCount;
	}

	public void setRevisionCount(Long revisionCount) {
		this.revisionCount = revisionCount;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Instant getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Instant createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Instant getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Instant updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

}
