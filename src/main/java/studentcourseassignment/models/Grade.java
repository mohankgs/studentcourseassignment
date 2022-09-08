package studentcourseassignment.models;

public class Grade {
    String grade;

    public Grade(String grade){
        this.grade = grade;
    }

    public Integer getPoints() {
        switch(grade){
            case "A":
                return 5;
            case "B":
                return 4;
            case "C":
                return 3;
            case "D":
                return 2;
            case "F":
                return 0;
            default:
                return null;
        }
    }

}
