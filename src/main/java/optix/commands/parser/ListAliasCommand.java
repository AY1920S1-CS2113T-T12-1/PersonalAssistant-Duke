package optix.commands.parser;

import optix.commands.Command;
import optix.commons.Model;
import optix.commons.Storage;
import optix.ui.Ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ListAliasCommand extends Command {

    /**
     * Processes user input to be stored, queried, modified in ShowMap,
     * to show response by program in ui and store existing data in Storage.
     *
     * @param model   The data structure holding all the information.
     * @param ui      The User Interface that reads user input and response to user.
     * @param storage The filepath of txt file which data are being stored.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) {
        StringBuilder systemMessage = new StringBuilder("Alias Settings:\n");
        // open target file
        File currentDir = new File(System.getProperty("user.dir"));
        File filePath = new File(currentDir.toString() + "\\src\\main\\data\\ParserPreferences.txt");
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                String[] details = data.split("\\|", 2);
                String alias = details[0];
                String command = details[1];
                systemMessage.append(alias).append(" : ").append(command).append('\n');
            }
            bufferedReader.close();
            fileReader.close();
            ui.setMessage(systemMessage.toString());
        } catch (IOException e) {
            ui.setMessage(e.getMessage());
        }
    }

    /**
     * Dummy command
     * @param details n.a.
     * @return n.a.
     */
    @Override
    public String[] parseDetails(String details) {
        return new String[0];
    }
}

