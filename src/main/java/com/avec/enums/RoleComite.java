package com.avec.enums;

public enum RoleComite {

	PRESIDENT("Président"), SECRETAIRE("Secrétaire"), TRESORIER("Trésorier"), COMPTEUR("Compteur"), AUCUN("Aucun rôle");

	private String description;

	RoleComite(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return description;
	}
}
