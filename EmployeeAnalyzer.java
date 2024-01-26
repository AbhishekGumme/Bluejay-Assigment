package assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmployeeAnalyzer {

    public static void main(String[] args) {
        // Replace 'Assignment_Timecard.txt' with the actual name of your file
        String filePath = "E:\\Resume\\Assignment_Timecard.txt";

        try {
            analyzeFile(filePath);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void analyzeFile(String filePath) throws IOException, ParseException {
        Map<String, EmployeeData> employees = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String name = parts[0].trim();
                    String position = parts[1].trim();

                    Date startTime = parseDate(parts[2].trim());
                    Date endTime = parseDate(parts[3].trim());

                    if (startTime == null || endTime == null) {
                        System.out.println("Invalid date format in line: " + line);
                        continue;
                    }

                    // Check for employees who have worked for 7 consecutive days
                    if (employees.containsKey(name)) {
                        employees.get(name).getDaysWorked().add(startTime);
                    } else {
                        EmployeeData employeeData = new EmployeeData(position);
                        employeeData.getDaysWorked().add(startTime);
                        employees.put(name, employeeData);
                    }

                    // Check for employees with less than 10 hours between shifts but greater than 1 hour
                    if (employees.containsKey(name) && employees.get(name).getDaysWorked().size() > 1) {
                        Date lastEndTime = employees.get(name).getEndTime();
                        long timeBetweenShifts = startTime.getTime() - lastEndTime.getTime();
                        if (timeBetweenShifts > 3600000 && timeBetweenShifts < 36000000) {
                            System.out.println(name + " (" + position + ") has less than 10 hours between shifts on "
                                    + new SimpleDateFormat("yyyy-MM-dd").format(startTime) + ".");
                        }
                    }

                    // Check for employees who have worked for more than 14 hours in a single shift
                    if (endTime.getTime() - startTime.getTime() > 50400000) { // 14 hours in milliseconds
                        System.out.println(name + " (" + position + ") has worked for more than 14 hours on "
                                + new SimpleDateFormat("yyyy-MM-dd").format(startTime) + ".");
                    }

                    employees.get(name).setEndTime(endTime);
                } else {
                    // Handle the case where the array does not have enough elements
                    System.out.println("Invalid line format: " + line);
                }
            }
        }
    }

    private static Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + dateString);
            return null;
        }
    }

    private static class EmployeeData {
        private String position;
        private Set<Date> daysWorked;
        private Date endTime;

        public EmployeeData(String position) {
            this.position = position;
            this.daysWorked = new HashSet<>();
        }

        public String getPosition() {
            return position;
        }

        public Set<Date> getDaysWorked() {
            return daysWorked;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
    }
}
