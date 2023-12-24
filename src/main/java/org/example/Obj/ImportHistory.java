package org.example.Obj;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入历史管理
 *
 * @author 张喆宇
 * Date 2023/12/17 19:42
 */
public class ImportHistory implements Serializable {
    private List<ImageObj> totalImageObj = new ArrayList<>();

    private String date = null;

    public ImportHistory(List<ImageObj> totalImageObj) {
        this.totalImageObj = totalImageObj;
        // 获取当前系统时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        this.date = formattedTime;
    }

    public List<ImageObj> getTotalImageObj() {
        return totalImageObj;
    }

    public String getDate() {
        return date;
    }
}
