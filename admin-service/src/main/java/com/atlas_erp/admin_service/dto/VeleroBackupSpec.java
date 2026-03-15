package com.atlas_erp.admin_service.dto;

public class VeleroBackupSpec {
    private java.util.List<String> includedNamespaces;
    private String ttl;

    public java.util.List<String> getIncludedNamespaces() {
        return includedNamespaces;
    }

    public void setIncludedNamespaces(java.util.List<String> includedNamespaces) {
        this.includedNamespaces = includedNamespaces;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
}
