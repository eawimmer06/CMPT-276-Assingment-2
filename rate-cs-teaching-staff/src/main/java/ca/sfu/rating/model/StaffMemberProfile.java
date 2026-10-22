package ca.sfu.rating.model;

/** Small polymorphic presentation model; role-specific classes provide their own titles. */
public abstract class StaffMemberProfile {
    public abstract String displayTitle();
}
