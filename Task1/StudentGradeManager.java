import java.util.ArrayList;
import java.util.Scanner;

class Student {
    String name;
    double score;

    Student(String name, double score) {
        this.name = name;
        this.score = score;
    }
}

public class StudentGradeManager{
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== STUDENT GRADE =====");
            System.out.println("1. Add Student");
            System.out.println("2. Modify Student");
            System.out.println("3. Delete Student");
            System.out.println("4. Display Summary Report");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addStudent(students, sc);
                    break;
                case 2:
                    modifyStudent(students, sc);
                    break;
                case 3:
                    deleteStudent(students, sc);
                    break;
                case 4:
                    displaySummary(students);
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }

    public static void addStudent(ArrayList<Student> students, Scanner sc) {
        sc.nextLine();
        System.out.print("Enter student name: ");
        String name = sc.nextLine();

        System.out.print("Enter score: ");
        double score = sc.nextDouble();

        students.add(new Student(name, score));
        System.out.println("Student added successfully!");
    }

    public static void modifyStudent(ArrayList<Student> students, Scanner sc) {
        if (students.isEmpty()) {
            System.out.println("No students available to modify!");
            return;
        }

        sc.nextLine(); 
        System.out.print("Enter name of student to modify: ");
        String nameToModify = sc.nextLine();

        for (Student s : students) {
            if (s.name.equalsIgnoreCase(nameToModify)) {
                System.out.print("Enter new name: ");
                s.name = sc.nextLine();

                System.out.print("Enter new score: ");
                s.score = sc.nextDouble();

                System.out.println("Student successfully modified!");
                return;
            }
        }
        System.out.println("Student not found!");
    }

    public static void deleteStudent(ArrayList<Student> students, Scanner sc) {
        if (students.isEmpty()) {
            System.out.println("No students available to delete!");
            return;
        }

        sc.nextLine(); 
        System.out.print("Enter name of student to delete: ");
        String nameToDelete = sc.nextLine();

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).name.equalsIgnoreCase(nameToDelete)) {
                students.remove(i);
                System.out.println("Student deleted successfully!");
                return;
            }
        }
        System.out.println("Student not found!");
    }

    public static void displaySummary(ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("\nNo student data available!");
            return;
        }

        double total = 0;
        double highest = students.get(0).score;
        double lowest = students.get(0).score;
        String highestName = students.get(0).name;
        String lowestName = students.get(0).name;

        System.out.println("\n===== STUDENT REPORT =====");
        for (Student s : students) {
            System.out.println("Name: " + s.name + " | Score: " + s.score);
            total += s.score;

            if (s.score > highest) {
                highest = s.score;
                highestName = s.name;
            }
            if (s.score < lowest) {
                lowest = s.score;
                lowestName = s.name;
            }
        }

        double average = total / students.size();

        System.out.println("\n--- Statistics ---");
        System.out.println("Average Score : " + average);
        System.out.println("Highest Score : " + highest + " (" + highestName + ")");
        System.out.println("Lowest Score  : " + lowest + " (" + lowestName + ")");
    }
}
