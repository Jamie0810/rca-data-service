package com.foxconn.iisd.rcadsvc.msg;

import com.foxconn.iisd.rcadsvc.domain.fa.SymptomType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class TestMsg {
    private List<String> names;
    private List<Integer> ids;
    private SymptomType type;
    private List<MultipartFile> files;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public SymptomType getType() {
        return type;
    }

    public void setType(SymptomType type) {
        this.type = type;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }
}
