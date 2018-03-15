package asuper.yt.cn.supermarket.entities;

/**
 * 合同审批表图片
 * Created by zengxiaowen on 2017/3/19.
 */
public class AppendixOne {

    private String id; //图片编号

    private String filePath; //图片地址

    private String fileType; //图片类型:AGREEMENT：纸质合同,AGREEMENT_COMMITMENT：合同承诺书,AGREEMENT_EXPLAIN:合同说明

    private int sortNumber;//图片排序序号

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
    }

    @Override
    public String toString() {
        return "AppendixOne{" +
                "id='" + id + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", sortNumber=" + sortNumber +
                '}';
    }
}
