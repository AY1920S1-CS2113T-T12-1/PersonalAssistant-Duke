package optix.commands.finance;

import optix.commands.seats.SellSeatCommand;
import optix.commands.shows.AddCommand;
import optix.commons.Model;
import optix.commons.Storage;
import optix.ui.Ui;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewProfitCommandTest {
    private Ui ui = new Ui();
    private static File currentDir = new File(System.getProperty("user.dir"));
    private static File filePath = new File(currentDir.toString() + "\\src\\test\\data\\testOptix");
    private Storage storage = new Storage(filePath);
    private Model model = new Model(storage);

    @Test
    void execute() {
        AddCommand addDummyShow = new AddCommand("dummy show name|20|10/5/2020");
        addDummyShow.execute(model, ui, storage);

        SellSeatCommand sellDummySeat = new SellSeatCommand("dummy show name|10/5/2020|A1 A2 A3");
        sellDummySeat.execute(model, ui, storage);

        ViewProfitCommand testCommand = new ViewProfitCommand("dummy show name|10/5/2020");
        testCommand.execute(model, ui, storage);

        String expected = "__________________________________________________________________________________\n"
                + "The profit for dummy show name on 10/5/2020 is 90.00\n"
                + "__________________________________________________________________________________\n";

        assertEquals(expected, ui.showCommandLine());

        ViewProfitCommand testCommand2 = new ViewProfitCommand("dummy show name|6/5/2020");
        testCommand2.execute(model, ui, storage);

        String expected2 = "__________________________________________________________________________________\n"
                + "☹ OOPS!!! The show cannot be found.\n"
                + "__________________________________________________________________________________\n";

        assertEquals(expected2, ui.showCommandLine());

        ViewProfitCommand testCommand3 = new ViewProfitCommand("wrong show name|10/5/2020");
        testCommand3.execute(model, ui, storage);

        String expected3 = "__________________________________________________________________________________\n"
                + "☹ OOPS!!! Did you get the wrong date or wrong show. \n"
                + "Try again!\n"
                + "__________________________________________________________________________________\n";

        assertEquals(expected3, ui.showCommandLine());

        filePath.deleteOnExit();
    }

    @AfterAll
    static void cleanUp() {
        File deletedFile = new File(filePath, "optix.txt");
        deletedFile.delete();
    }
}
