import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.lang.StringBuilder;

public class WardenService {


    /* Read wardens from CSV file and add them to a list containing all the wardens */
    public static List<Warden> readWardensFromFile(String filePath){

        List<Warden> wardens = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            reader.readLine();

            while((line = reader.readLine()) != null){
                String[] data = line.split(",", -1);

                Warden warden = new Warden(
                        data[0],                            // first name
                        data[1].isEmpty() ? null : data[1], // last name
                        data[2],                            // identifier value
                        data[3].isEmpty() ? null : data[3], // email
                        data[4],                            // starting date
                        data[5].isEmpty() ? null : data[5], // end date
                        Integer.parseInt(data[6]),          // employment id
                        Integer.parseInt(data[7]),          // role id
                        Integer.parseInt(data[8]),          // clearance id
                        Integer.parseInt(data[9])           // identifier type id
                );

                wardens.add(warden);

            }

        } catch(IOException e){
            System.out.println("There was an error reading the file.");
        }

        return wardens;
    }

    /* Check if the warden already exists in the system */
    public static boolean checkDuplicate(List<Warden> wardens, String identifier){

        for(Warden warden : wardens){
            if(warden.getIdentifierValue().equalsIgnoreCase(identifier)){
                return true;
            }
        }

        return false;
    }

    /* Check if an entry is empty */
    public static boolean checkNotBlank(String entry){

        return entry!= null && !entry.trim().isEmpty();
    }

    /* Check if the date is valid */
    public static boolean checkValidDate(String date){

        try{
            LocalDate.parse(date);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    /* Check if  */

    /* Display all the wardens */
    public static void displayWardens(List<Warden> wardens){

        if(wardens.isEmpty()){
            System.out.println("There are no wardens found.");
            return;
        }

        String[] headers = {"First name", "Last name", "ID value", "Email", "Start Date",
                            "End Date", "Employment", "Role", "Clearance", "ID type"};

        List<Function<Warden, String>> getters = List.of(
                Warden::getFirstName,
                Warden::getLastName,
                Warden::getIdentifierValue,
                Warden::getEmail,
                Warden::getStartingDate,
                Warden::getEndDate,
                w -> String.valueOf(w.getEmploymentId()),
                w -> String.valueOf(w.getRoleId()),
                w -> String.valueOf(w.getClearanceId()),
                w -> String.valueOf(w.getIdentifierTypeId())
        );

        int[] widths = new int[headers.length];
        for(int i = 0; i < headers.length; i++){
            widths[i] = getMaxWidth(wardens, getters.get(i), headers[i]);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(int width : widths){
            stringBuilder.append("%-").append(width + 2).append("s");
        }
        stringBuilder.append("\n");
        String format = stringBuilder.toString();

        System.out.printf(format, (Object[]) headers);

        int totalWidth = 0;
        for(int width  : widths){
            totalWidth += width + 2;
        }
        System.out.println("-".repeat(totalWidth));

        for(Warden warden : wardens){
            String[] row = new String[getters.size()];

            for(int i = 0; i < getters.size(); i++){
                String value = getters.get(i).apply(warden);
                row[i] = Objects.requireNonNullElse(value, "");
            }

            System.out.printf(format, (Object[]) row);

        }
    }

    /* Helper method for displayWardens to determine the max width needed for a column */
    public static int getMaxWidth(List<Warden> wardens, Function<Warden,String> getter, String header){

        int max = header.length();

        for(Warden warden : wardens){
            String value = getter.apply(warden);

            if(value == null){
                value = "";
            }

            if(value.length() > max){
                max = value.length();
            }
        }

        return max;
    }

}
