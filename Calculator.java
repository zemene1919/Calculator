







import javax.swing.*;
import java.awt.*;

public class Calculator extends JFrame {
    private final JTextField display = new JTextField("0");
    private double firstNumber = 0;
    private String operator = "";
    private boolean startNewNumber = true;

    public Calculator() {
        setTitle("Java Calculator");
        setSize(320, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        display.setFont(new Font("Arial", Font.PLAIN, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(4, 4, 6, 6));
        String[] labels = {
                "7", "8", "9", "÷",
                "4", "5", "6", "×",
                "1", "2", "3", "−",
                "C", "0", "=", "+"
        };

        for (String label : labels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 22));
            button.addActionListener(event -> handleButton(label));
            buttons.add(button);
        }

        add(buttons, BorderLayout.CENTER);
        setVisible(true);
    }

    private void handleButton(String input) {
        if (input.matches("\\d")) {
            if (startNewNumber || display.getText().equals("Error")) {
                display.setText(input);
                startNewNumber = false;
            } else {
                display.setText(display.getText() + input);
            }
            return;
        }

        if (input.equals("C")) {
            display.setText("0");
            firstNumber = 0;
            operator = "";
            startNewNumber = true;
            return;
        }

        if (input.equals("=")) {
            calculate();
            operator = "";
            startNewNumber = true;
            return;
        }

        firstNumber = Double.parseDouble(display.getText());
        operator = input;
        startNewNumber = true;
    }

    private void calculate() {
        if (operator.isEmpty()) return;

        double secondNumber = Double.parseDouble(display.getText());
        double result;

        switch (operator) {
            case "+" -> result = firstNumber + secondNumber;
            case "−" -> result = firstNumber - secondNumber;
            case "×" -> result = firstNumber * secondNumber;
            case "÷" -> {
                if (secondNumber == 0) {
                    display.setText("Error");
                    return;
                }
                result = firstNumber / secondNumber;
            }
            default -> { return; }
        }

        display.setText(String.valueOf(result));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}