package com.utravel.app.entity;

public class VersionBean {

    /**
     * binary : {"fsize":46196328}
     * build : 43
     * changelog : 1、更新余额充值功能 2、调整提现功能，加入支付密码验证
     * direct_install_url : http://download.fir.im/v2/app/install/5ad4d0ff548b7a055fa93000?download_token=8ca0726384aff8b771658a5d49cf169a&source=update
     * installUrl : http://download.fir.im/v2/app/install/5ad4d0ff548b7a055fa93000?download_token=8ca0726384aff8b771658a5d49cf169a&source=update
     * install_url : http://download.fir.im/v2/app/install/5ad4d0ff548b7a055fa93000?download_token=8ca0726384aff8b771658a5d49cf169a&source=update
     * name : 海峡给
     * update_url : http://fir.im/hz1y
     * updated_at : 1528718635
     * version : 43
     * versionShort : 5.2
     */

    private BinaryBean binary;
    private String build;
    private String changelog;
    private String direct_install_url;
    private String installUrl;
    private String install_url;
    private String name;
    private String update_url;
    private int updated_at;
    private String version;
    private String versionShort;

    public BinaryBean getBinary() {
        return binary;
    }

    public void setBinary(BinaryBean binary) {
        this.binary = binary;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getDirect_install_url() {
        return direct_install_url;
    }

    public void setDirect_install_url(String direct_install_url) {
        this.direct_install_url = direct_install_url;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public static class BinaryBean {
        /**
         * fsize : 46196328
         */

        private int fsize;

        public int getFsize() {
            return fsize;
        }

        public void setFsize(int fsize) {
            this.fsize = fsize;
        }
    }
}
