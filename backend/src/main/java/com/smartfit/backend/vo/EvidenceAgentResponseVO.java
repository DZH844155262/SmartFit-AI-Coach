package com.smartfit.backend.vo;

import java.util.List;

public class EvidenceAgentResponseVO {

    // 对用户问题最直接的结论
    private String directConclusion;

    // 论文共同支持的核心发现
    private List<String> keyFindings;

    // 当前证据有什么不足、不能过度推断什么
    private List<String> limitations;

    // 本次真正使用的论文
    private List<EvidenceItemVO> evidence;


    public String getDirectConclusion() {
        return directConclusion;
    }

    public void setDirectConclusion(String directConclusion) {
        this.directConclusion = directConclusion;
    }

    public List<String> getKeyFindings() {
        return keyFindings;
    }

    public void setKeyFindings(List<String> keyFindings) {
        this.keyFindings = keyFindings;
    }

    public List<String> getLimitations() {
        return limitations;
    }

    public void setLimitations(List<String> limitations) {
        this.limitations = limitations;
    }

    public List<EvidenceItemVO> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<EvidenceItemVO> evidence) {
        this.evidence = evidence;
    }
}