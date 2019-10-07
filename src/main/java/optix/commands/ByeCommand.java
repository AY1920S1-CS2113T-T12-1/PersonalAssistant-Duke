package optix.commands;

import optix.Ui;
import optix.core.Storage;
import optix.util.ShowMap;

public class ByeCommand extends  Command {

    private static final String MESSAGE_BYE = "Bye. Hope to see you again soon!\n";

    @Override
    public void execute(ShowMap shows, Ui ui, Storage storage) {
        storage.write(shows);
        ui.setMessage(MESSAGE_BYE);
        ui.exitOptix();
    }

    /**
     * Exits Optix.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
