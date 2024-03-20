package edu.iu.c322.midterm.repository;

import edu.iu.c322.midterm.model.Question;
import edu.iu.c322.midterm.model.Quiz;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FileRepository {
    private String IMAGES_FOLDER_PATH = "quizzes/questions/images";
    private static final String NEW_LINE = System.lineSeparator();
    private static final String QUESTION_DATABASE_NAME = "quizzes/questions.txt";
    private static final String QUIZ_DATABASE_NAME = "quizzes/quizzes.txt";

    public FileRepository() {
        File imagesDirectory = new File(IMAGES_FOLDER_PATH);
        if(!imagesDirectory.exists()) {
            imagesDirectory.mkdirs();
        }
    }

    private static void appendToFile(Path path, String content)
            throws IOException {
        Files.write(path,
                content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    public int add(Question question) throws IOException {
        Path path = Paths.get(QUESTION_DATABASE_NAME);
        List<Question> questions = findAllQuestions();
        int id = 0;
        for(Question q : questions) {
            if(q.getId() > id) {
                id = q.getId();
            }
        }
        id = id + 1;
        question.setId(id);
        String data = question.toLine();
        appendToFile(path, data + NEW_LINE);
        return id;
    }

    public int add(Quiz quiz) throws IOException {
        Path path = Paths.get(QUIZ_DATABASE_NAME);
        List<Quiz> quizzes = findAllQuizzes();
        int id = 0;
        for(Quiz q : quizzes) {
            if(q.getId() > id) {
                id = q.getId();
            }
        }
        id = id + 1;
        quiz.setId(id);
        String data = quiz.toLine(id);
        appendToFile(path, data + NEW_LINE);
        return id;
    }



    public List<Question> findAllQuestions() throws IOException {
        List<Question> result = new ArrayList<>();
        Path path = Paths.get(QUESTION_DATABASE_NAME);
        if (Files.exists(path)) {
            List<String> data = Files.readAllLines(path);
            for (String line : data) {
                if(line.trim().length() != 0) {
                    Question q = Question.fromLine(line);
                    result.add(q);
                }
            }
        }
        return result;
    }

    public Quiz getQuiz(int id) throws IOException {
        List<Quiz> quizzes = findAllQuizzes();
        for (Quiz quiz : quizzes) {
            if (quiz.getId() == id) {
                return quiz;
            }
        }
        throw new IOException("404 Not Found");
    }

    public List<Quiz> findAllQuizzes() throws IOException {
        List<Quiz> result = new ArrayList<>();
        Path path = Paths.get(QUIZ_DATABASE_NAME);
        if (Files.exists(path)) {
            List<String> data = Files.readAllLines(path);
            for (String line : data) {
                if(line.trim().length() != 0) {
                    Quiz q = Quiz.fromLine(line);
                    result.add(q);
                }
            }
        }
        for (Quiz quiz : result) {
            List<Question> questions = find(quiz.getQuestionIds());
            quiz.setQuestions(questions);
        }
        return result;
    }


    public List<Question> find(String answer) throws IOException {
        List<Question> animals = findAllQuestions();
        List<Question> result = new ArrayList<>();
        for (Question question : animals) {
            if (answer != null && !question.getAnswer().trim().equalsIgnoreCase(answer.trim())) {
                continue;
            }
            result.add(question);
        }
        return result;
    }

    public List<Question> find(List<Integer> ids) throws IOException {
        List<Question> questions = findAllQuestions();
        List<Question> result = new ArrayList<>();
        for (int id : ids) {
            Question q = questions.stream().filter(x -> x.getId() == id).toList().get(0);
            result.add(q);
        }
        return result;
    }

    public List<Quiz> findQuizzes(List<Integer> ids) throws IOException {
        List<Quiz> quizzes = findAllQuizzes();
        List<Quiz> result = new ArrayList<>();
        for (int id : ids) {
            Quiz q = quizzes.stream().filter(x -> x.getId() == id).toList().get(0);
            result.add(q);
        }
        return result;
    }

    public Question get(Integer id) throws IOException {
        List<Question> questions = findAllQuestions();
        for (Question question : questions) {
            if (question.getId() == id) {
                return question;
            }
        }
        return null;
    }

    public boolean updateImage(int id, MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getContentType());

        String fileExtension = ".png";
        Path path = Paths.get(IMAGES_FOLDER_PATH
                + "/" + id + fileExtension);
        System.out.println("The file " + path + " was saved successfully.");
        file.transferTo(path);
        return true;
    }

    public byte[] getImage(int id) throws IOException {
        String fileExtension = ".png";
        Path path = Paths.get(IMAGES_FOLDER_PATH
                + "/" + id + fileExtension);
        byte[] image = Files.readAllBytes(path);
        return image;
    }

    public void updateQuiz(int id, Quiz newQuiz) throws IOException {
        List<Quiz> quizzes = findAllQuizzes();
        boolean found = false;

        System.out.println(newQuiz);

        for (int i = 0; i < quizzes.size(); i++) {
            if (quizzes.get(i).getId() == id) {
                found = true;
                Quiz existingQuiz = quizzes.get(i);

                if (newQuiz.getTitle() != null) {
                    existingQuiz.setTitle(newQuiz.getTitle());
                }

                if (newQuiz.getQuestionIds() != null) {
                    existingQuiz.setQuestionIds(newQuiz.getQuestionIds());
                }

                quizzes.set(i, existingQuiz);
                break;
            }
        }

        if (!found) {
            throw new IOException("404 Not Found");
        }

        Path path = Paths.get(QUIZ_DATABASE_NAME);
        Files.write(path, "".getBytes());
        for (Quiz q : quizzes) {
            String data = q.toLine(q.getId());
            appendToFile(path, data + NEW_LINE);
        }
    }

}
