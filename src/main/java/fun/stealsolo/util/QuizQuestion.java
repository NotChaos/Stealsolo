package fun.stealsolo.util;

public record QuizQuestion(String name, String question, java.util.@org.jetbrains.annotations.NotNull List<String> answers) {

    public boolean isValidAnswer(String answer) {
        if (answer == null || answer.isEmpty()) {
            return false;
        }
        answers.replaceAll(String::toLowerCase);
        return answers.contains(answer.toLowerCase());
    }
}
