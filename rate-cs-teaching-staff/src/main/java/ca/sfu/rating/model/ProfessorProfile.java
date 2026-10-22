package ca.sfu.rating.model;

public class ProfessorProfile extends StaffMemberProfile {
    @Override
    public String displayTitle() {
        return "Professor";
    }
}
