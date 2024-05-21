package ch.uzh.ifi.hase.soprafs24.rest.dto.group;

public class GroupRankingGetDTO {
    private String id;
    private String initials;
    private int rank;

    public void setId(String id){
        this.id= id;
    }
    public String getId(){
        return this.id;
    }
    public void setInitials(String initials){
        this.initials= initials;
    }
    public String getInitials(){
        return this.initials;
    }
    public void setRank(int rank){
        this.rank= rank;
    }
    public int getRank(){
        return this.rank;
    }
}
