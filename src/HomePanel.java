import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HomePanel extends JPanel {

    private final String word;
    private String wordMask;
    private final JList<String> usedLetterList;
    private final DefaultListModel<String> usedLetterModel;
    private int countError;
    private final JLabel countLabel;
    private final JTextField letterField;
    private final JLabel wordLabel;

    public HomePanel(){
        this.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;

        ArrayList<String> wordList = new ArrayList<>();

        Database database = new Database();
        try {
            ResultSet resultat = database.getAll();

            while (resultat.next())
                wordList.add(database.getResultSet().getString("mot"));

            database.closeConnection();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        this.word = wordList.get((new Random()).nextInt(wordList.size()));
        this.wordMask = "";
        for (int i = 0; i < this.word.length(); i++)
            this.wordMask += "#";

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        this.wordLabel = new JLabel(this.word){{
            setFont(getFont().deriveFont(new HashMap<TextAttribute, Object>(){{
                this.put(TextAttribute.TRACKING, 0.5);
            }}));
        }};
        this.formatWord();
        this.add(this.wordLabel, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        this.usedLetterModel = new DefaultListModel<>();
        this.usedLetterList = new JList<>(this.usedLetterModel){{
            setMaximumSize(new Dimension(50, 300));
            setSize(new Dimension(50, 300));
            setVisibleRowCount(10);
            setLayoutOrientation(JList.VERTICAL);
        }};
        JScrollPane scrollPane = new JScrollPane(this.usedLetterList){{
            setSize(50, 300);
            setMaximumSize(new Dimension(50, 300));
        }};
        this.add(scrollPane, gridBagConstraints);

        this.countError = 0;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.countLabel = new JLabel(this.countError + "/10");
        this.add(this.countLabel, gridBagConstraints);

        this.letterField = new JTextField(){{
            setColumns(2);
            setFont(new Font("serif", Font.PLAIN, 16));
        }};
        this.letterField.addActionListener(e -> {
            if (this.letterField.getText().isEmpty())
                return;

            this.validateLetter();
            this.letterField.grabFocus();
        });
        this.letterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);

                JTextField textField = (JTextField) e.getSource();

                if (textField.getText().length() > 1)
                    letterField.setText(String.valueOf(letterField.getText().charAt(1)));

                try{
                    Integer.valueOf(textField.getText());

                    textField.setText("");
                }catch(NumberFormatException ignored){}

                letterField.setText(letterField.getText().toUpperCase());
            }
        });
        JButton letterButton = new JButton("Valider");
        letterButton.grabFocus();
        letterButton.addActionListener(e -> {
            if (this.letterField.getText().isEmpty())
                return;

            this.validateLetter();
        });

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        JPanel letterPanel = new JPanel();
        letterPanel.setLayout(new BoxLayout(letterPanel, BoxLayout.X_AXIS));
        letterPanel.add(this.letterField);
        letterPanel.add(letterButton);
        this.add(letterPanel, gridBagConstraints);
    }

    private void formatWord(){
        this.wordLabel.setText("");

        for (int i = 0; i < this.word.length(); i++) {

            if (this.wordMask.charAt(i) == '#')
                this.wordLabel.setText(this.wordLabel.getText() + '_');
            else
                this.wordLabel.setText(this.wordLabel.getText() + this.word.charAt(i));
        }
    }

    private void validateLetter(){
        String letter = this.letterField.getText().toUpperCase();
        this.letterField.setText("");

        if (this.word.contains(letter)){
            StringBuilder string = new StringBuilder(this.wordMask);
            for (int i = 0; i < this.word.length(); i++)
                if (this.word.charAt(i) == letter.charAt(0))
                    string.setCharAt(i, letter.charAt(0));

            this.wordMask = string.toString();

            this.formatWord();

            if (this.wordMask.equals(this.word)){
                JOptionPane.showMessageDialog(null, "Bien joué, le mot était " + this.word);
                this.restart();
            }

            return;
        }

        if (this.addUsedLetter(letter)){
            this.countError++;
            this.countLabel.setText(this.countError + "/10");
            if (this.countError == 10){
                JOptionPane.showMessageDialog(null, "Tu as perdu, le mot était " + this.word);
                this.restart();
            }
        }
    }

    private boolean addUsedLetter(String letter){
        if (this.isUsedLetter(letter))
            return false;

        this.usedLetterModel.addElement(letter.toUpperCase());

        return true;
    }

    private boolean isUsedLetter(String letter){
        for (int i = 0; i < this.usedLetterList.getModel().getSize(); i++)
            if (this.usedLetterList.getModel().getElementAt(i).equals(letter))
                return true;

        return false;
    }
    private void restart(){
        Main.window.setContentPane(new HomePanel());
        Main.window.revalidate();
    }
}
