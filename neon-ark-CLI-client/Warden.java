public class Warden {

    private String firstName;
    private String lastName;
    private String identifierValue;
    private String email;
    private String startingDate;
    private String endDate;
    private int employmentId;
    private int roleId;
    private int clearanceId;
    private int identifierTypeId;

    public Warden(String firstName, String lastName, String identifierValue, String email,
                  String startingDate, String endDate, int employmentId, int roleId, int clearanceId,
                  int identifierTypeId){

        this.firstName = firstName;
        this.lastName = lastName;
        this.identifierValue = identifierValue;
        this.email = email;
        this.startingDate = startingDate;
        this.endDate = endDate;
        this.employmentId = employmentId;
        this.roleId = roleId;
        this.clearanceId = clearanceId;
        this.identifierTypeId = identifierTypeId;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getIdentifierValue() { return identifierValue; }
    public String getEmail() { return email; }
    public String getStartingDate() { return startingDate; }
    public String getEndDate() { return endDate; }
    public int getEmploymentId() { return employmentId; }
    public int getRoleId() { return roleId; }
    public int getClearanceId() { return clearanceId; }
    public int getIdentifierTypeId() { return identifierTypeId; }
}
