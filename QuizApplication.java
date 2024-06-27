import java.util.*;
import java.util.concurrent.*;

class QuizQuestion{
    private String question;
    private List<String> options;
    private int correctOptionIndex;

    public QuizQuestion(String question, List<String> options, int correctOptionIndex) {
        this.question = question;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}

class Quiz {
    private List<QuizQuestion> questions;
    private int score;
    private int correctAnswers;
    private int incorrectAnswers;
    private Scanner scanner;

    public Quiz(List<QuizQuestion> questions) {
        this.questions = questions;
        this.score = 0;
        this.correctAnswers = 0;
        this.incorrectAnswers = 0;
        this.scanner = new Scanner(System.in);
    }

    public void startQuiz() {
        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion currentQuestion = questions.get(i);
            System.out.println("Question " + (i + 1) + ": " + currentQuestion.getQuestion());
            List<String> options = currentQuestion.getOptions();
            for (int j = 0; j < options.size(); j++) {
                System.out.println((j + 1) + ". " + options.get(j));
            }

            // Start timer
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                System.out.println("Time's up!");
                executor.shutdownNow();
            };
            ScheduledFuture<?> future = executor.schedule(task, 2, TimeUnit.MINUTES); // Timer set to 2 minutes

            // Check remaining time and notify
            ScheduledExecutorService notificationExecutor = Executors.newScheduledThreadPool(1);
            Runnable notificationTask = () -> {
                long remainingTime = future.getDelay(TimeUnit.SECONDS);
                if (remainingTime > 0) {
                    long minutes = remainingTime / 60;
                    long seconds = remainingTime % 60;
                    System.out.println("Time remaining: " + minutes + " minutes " + seconds + " seconds");
                } else {
                    System.out.println("Time's up!");
                    executor.shutdownNow();
                }
            };
            notificationExecutor.scheduleAtFixedRate(notificationTask, 0, 10, TimeUnit.SECONDS); // Check every 10 seconds

            // Accept user input
            System.out.print("Enter your choice (press Enter to skip): ");
            String userInput = scanner.nextLine();
            if (!userInput.isEmpty() && !future.isDone()) {
                try {
                    int userChoice = Integer.parseInt(userInput);
                    int correctOptionIndex = currentQuestion.getCorrectOptionIndex();
                    if (userChoice == correctOptionIndex + 1) {
                        System.out.println("Correct!");
                        score++;
                        correctAnswers++;
                    } else {
                        System.out.println("Incorrect! Correct answer is: " + options.get(correctOptionIndex));
                        incorrectAnswers++;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Moving to the next question.");
                    incorrectAnswers++;
                }
            } else {
                System.out.println("Time's up! Moving to the next question.");
                incorrectAnswers++;
            }

            // Stop timer and notification task
            future.cancel(true);
            notificationExecutor.shutdownNow();
        }

        // Display results
        System.out.println("Quiz completed!");
        System.out.println("Your score: " + score + "/" + questions.size());
        System.out.println("Correct answers: " + correctAnswers);
        System.out.println("Incorrect answers: " + incorrectAnswers);
    }
}

public class QuizApplication {
    public static void main(String[] args) {
        List<QuizQuestion> questions = new ArrayList<>();
        questions.add(new QuizQuestion("Which keyword is used to define a constant in Java?",
                Arrays.asList("a) constant", "b) final", "c) static", "d) const"), 1));
        questions.add(new QuizQuestion("Which collection class allows you to associate a unique key with a value?",
                Arrays.asList("a) ArrayList", "b) LinkedList", "c) HashMap", "d) HashSet"), 2));
        questions.add(new QuizQuestion("Which company originally developed Java?",
                Arrays.asList("a) Microsoft", "b) IBM", "c) Sun Microsystems", "d) Oracle Corporation"), 2));
        questions.add(new QuizQuestion("Who is often referred to as the Father of Java?",
                Arrays.asList("a) Bill Gates", "b) James Gosling", "c) Tim Berners-Lee", "d) Larry Page"), 1));
        questions.add(new QuizQuestion("What is the primary use of the Java Virtual Machine (JVM)?",
                Arrays.asList("a) To compile Java source code into bytecode", "b) To execute Java bytecode on different platforms", "c) To manage database operations in Java applications", "d) To provide a graphical user interface for Java applications"), 1));
        Quiz quiz = new Quiz(questions);
        quiz.startQuiz();
    }
}
