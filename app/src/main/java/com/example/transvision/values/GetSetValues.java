package com.example.transvision.values;

public class GetSetValues {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id="";
   private String user_name;

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    private String user_role;

    public String getSubdiv_code() {
        return subdiv_code;
    }

    public void setSubdiv_code(String subdiv_code) {
        this.subdiv_code = subdiv_code;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    private String subdiv_code;

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    private String item_name="";

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;

}
