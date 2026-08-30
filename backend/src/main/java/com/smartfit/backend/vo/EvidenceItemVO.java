package com.smartfit.backend.vo;

public class EvidenceItemVO {

    // DeepSeek选择使用的真实PMID
    private String pmid;

    // 以下三个字段最终由Java从PubMed真实结果中补充
    private String title;

    private String journal;

    private String year;

    // 这篇论文对当前问题支持的核心观点
    private String claim;


    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }
}