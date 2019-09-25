package optix.commands;

import optix.Ui;
import optix.core.Show;
import optix.core.Storage;
import optix.util.ShowMap;

import java.time.LocalDate;

public class DeleteCommand extends Command {
    private String[] showNames;

    public DeleteCommand(String[] showNames) {
        this.showNames = showNames;
    }

    @Override
    public void execute(ShowMap shows, Ui ui, Storage storage) {
        String message;
        for (String show: this.showNames) {
            for (LocalDate iterDate: )
        }

    }



}
