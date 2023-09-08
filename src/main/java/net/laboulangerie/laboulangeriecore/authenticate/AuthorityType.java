package net.laboulangerie.laboulangeriecore.authenticate;

public enum AuthorityType {
    PLAYER("p", "joueur"),
    TOWN("t", "ville"),
    NATION("n", "nation"),
    INVALID(null, null);

    private String prefix;
    private String suffix;

    private AuthorityType(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * Prefix used in the persistent value
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Suffix used in the lore
     * 
     * @return
     */
    public String getSuffix() {
        return suffix;
    }
}
