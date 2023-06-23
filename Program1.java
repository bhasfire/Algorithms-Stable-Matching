/*
 * Name: Boris
 * EID: bdh2778
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Your solution goes in this class.
 *
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 *
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {


    /**
     * Determines whether a candidate Matching represents a solution to the stable matching problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    @Override
    public boolean isStableMatching(Matching problem) {
        ArrayList<Integer> student_matching = problem.getStudentMatching();
        ArrayList<ArrayList<Integer>> student_preference = problem.getStudentPreference();
        ArrayList<ArrayList<Integer>> highschool_preference = problem.getHighSchoolPreference();

        for (int student = 0; student < student_matching.size(); student++) {
            int assigned_highschool = student_matching.get(student);

            // check all highschools higher on student's preference list
            for (int preferred_highschool : student_preference.get(student)) {
                if (preferred_highschool == assigned_highschool) {
                    break; // All remaining schools are less preferred
                }

                ArrayList<Integer> preferred_highschool_prefs = highschool_preference.get(preferred_highschool);
                int assigned_student_rank = preferred_highschool_prefs.indexOf(student);
                int current_student_rank = preferred_highschool_prefs.indexOf(student_matching.get(preferred_highschool));

                // If the preferred high school prefers this student to their currently assigned student, return false
                if (assigned_student_rank < current_student_rank) {
                    return false;
                }
            }
        }

        return true; // No instability found

    }

    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_studentoptimal(Matching problem) {
        int n = problem.getStudentCount();
        int m = problem.getHighSchoolCount();

        ArrayList<ArrayList<Integer>> student_pref = problem.getStudentPreference();
        ArrayList<ArrayList<Integer>> highschool_pref = problem.getHighSchoolPreference();
        ArrayList<Integer> highschool_spots = problem.getHighSchoolSpots();

        int[] school_capacity = new int[m];
        for (int i = 0; i < m; i++) {
            school_capacity[i] = highschool_spots.get(i);
        }

        ArrayList<Integer> student_matching = new ArrayList<>(Collections.nCopies(n, -1));
        int[] next_school_to_propose = new int[n];

        Queue<Integer> free_students = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            free_students.add(i);
        }

        while (!free_students.isEmpty()) {
            int student = free_students.poll();
            boolean matched = false;

            while (next_school_to_propose[student] < student_pref.get(student).size() && !matched) {
                int school = student_pref.get(student).get(next_school_to_propose[student]);

                if (school_capacity[school] > 0) {
                    student_matching.set(student, school);
                    school_capacity[school]--;
                    matched = true;
                } else {
                    int least_preferred_student = -1;
                    for (int j = 0; j < n; j++) {
                        if (student_matching.get(j) == school) {
                            if (least_preferred_student == -1 ||
                                highschool_pref.get(school).indexOf(j) > highschool_pref.get(school).indexOf(least_preferred_student)) {
                                least_preferred_student = j;
                            }
                        }
                    }

                    if (highschool_pref.get(school).indexOf(student) < highschool_pref.get(school).indexOf(least_preferred_student)) {
                        student_matching.set(student, school);
                        student_matching.set(least_preferred_student, -1);
                        free_students.add(least_preferred_student);
                        matched = true;
                    }
                }
                
                next_school_to_propose[student]++;
            }

            if (!matched) {
                //System.out.println("Student " + student + " has no more schools to propose to.");
            }
        }

        problem.setStudentMatching(student_matching);
        return problem;
    }


    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *  
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_highschooloptimal(Matching problem) {
    // number of students and high schools
    int n = problem.getStudentCount();
    int m = problem.getHighSchoolCount();

    // create preference order for students and high schools
    ArrayList<ArrayList<Integer>> student_pref = problem.getStudentPreference();
    ArrayList<ArrayList<Integer>> highschool_pref = problem.getHighSchoolPreference();

    // capacity for each high school
    ArrayList<Integer> highschool_spots = problem.getHighSchoolSpots();

    // array to store current capacity of each high school
    int[] school_capacity = new int[m];
    for (int i = 0; i < m; i++) {
        school_capacity[i] = highschool_spots.get(i);
    }

    // array to store the current school of each student, initially all -1 (unmatched)
    ArrayList<Integer> student_matching = new ArrayList<>(Collections.nCopies(n, -1));

    // array to store next student to propose for each school
    int[] next_student_to_propose = new int[m];

    // continue until no proposals can be made
    boolean proposals_possible = true;
    while (proposals_possible) {
        proposals_possible = false;
        for (int school = 0; school < m; school++) {
            while (school_capacity[school] > 0 && next_student_to_propose[school] < n) {
                proposals_possible = true;
                int student = highschool_pref.get(school).get(next_student_to_propose[school]);
                int matched_school = student_matching.get(student);
                if (matched_school == -1 || student_pref.get(student).indexOf(school) < student_pref.get(student).indexOf(matched_school)) {
                    student_matching.set(student, school);
                    if (matched_school != -1)
                        school_capacity[matched_school]++;
                    school_capacity[school]--;
                }
                next_student_to_propose[school]++;
            }
        }
    }

    problem.setStudentMatching(student_matching);
    return problem;
    }
}