/*
 * COMMAND PATTERN
 *
 * Wrap a request (a method call) into its OWN object, so it can be stored,
 * queued, logged and undone later - instead of being fired on the spot.
 *
 * Here: a smart-home remote control for a Light and an AC.
 *
 * The naive way is one method per button, with the remote holding every device:
 *     public void pressLightOn()  { light.on();  lastAction = "LIGHT_ON"; }
 *     public void pressACOff()    { ac.off();    lastAction = "AC_OFF";   }
 *     public void pressUndo()     { switch (lastAction) { ... } }
 * A new device means editing the remote, and undo is a switch on a string
 * that remembers exactly ONE action.
 *
 * Command: each action becomes a small object that knows its receiver and
 * knows how to reverse itself. The remote just holds Command objects.
 *
 * Result: the remote has no Light, no AC, no if, no switch - and unlimited
 * undo is a Stack<Command>.
 *
 * Compile & run:  javac Command.java  &&  java Main
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// ========== RECEIVERS ==========
// The objects that actually do the work. They know nothing about commands.

class Light {
    public void on() {
        System.out.println("   Light turned ON");
    }

    public void off() {
        System.out.println("   Light turned OFF");
    }
}

class AC {
    public void on() {
        System.out.println("   AC turned ON");
    }

    public void off() {
        System.out.println("   AC turned OFF");
    }
}

// ========== COMMAND ==========
// The contract: every request can be executed and reversed.

public interface Command {
    void execute();

    void undo();
}

// ========== CONCRETE COMMANDS ==========
// One class per action. Each one is BORN holding its receiver.

class LightOnCommand implements Command {
    private Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }

    @Override
    public void undo() {
        light.off(); // the command itself knows its reversal
    }
}

class LightOffCommand implements Command {
    private Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.off();
    }

    @Override
    public void undo() {
        light.on();
    }
}

class ACOnCommand implements Command {
    private AC ac;

    public ACOnCommand(AC ac) {
        this.ac = ac;
    }

    @Override
    public void execute() {
        ac.on();
    }

    @Override
    public void undo() {
        ac.off();
    }
}

class ACOffCommand implements Command {
    private AC ac;

    public ACOffCommand(AC ac) {
        this.ac = ac;
    }

    @Override
    public void execute() {
        ac.off();
    }

    @Override
    public void undo() {
        ac.on();
    }
}

// ========== MACRO COMMAND ==========
// A Command that holds other Commands (Composite + Command).
// To the remote it looks like any other single command.

class MacroCommand implements Command {
    private List<Command> commands;

    public MacroCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        for (Command c : commands) {
            c.execute();
        }
    }

    @Override
    public void undo() {
        // IMPORTANT: undo runs in REVERSE order (A->B->C undoes as C->B->A)
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
}

// ========== INVOKER ==========
// Triggers commands. Holds no Light, no AC, no if-else - only Command objects.

class RemoteControl {

    private Command[] buttons = new Command[4]; // 4 programmable slots
    private Stack<Command> commandHistory = new Stack<>();

    // Buttons are assigned at RUNTIME - any slot can do anything
    public void setCommand(int slot, Command command) {
        buttons[slot] = command;
    }

    public void pressButton(int slot) {
        if (buttons[slot] == null) {
            System.out.println("   No command assigned to slot " + slot);
            return;
        }
        buttons[slot].execute();
        commandHistory.push(buttons[slot]); // remember it for undo
    }

    // Unlimited undo - two lines, and they never change when a device is added
    public void pressUndo() {
        if (commandHistory.isEmpty()) {
            System.out.println("   Nothing left to undo");
            return;
        }
        commandHistory.pop().undo();
    }
}

// ========== CLIENT ==========
// Creates the receivers and the commands, and wires them into slots.
// This is the ONLY place that knows a Light and an AC exist.

class Main {
    public static void main(String[] args) {

        // Receivers
        Light light = new Light();
        AC ac = new AC();

        // Invoker
        RemoteControl remote = new RemoteControl();

        // Wiring: slot -> command
        remote.setCommand(0, new LightOnCommand(light));
        remote.setCommand(1, new LightOffCommand(light));
        remote.setCommand(2, new ACOnCommand(ac));
        remote.setCommand(3, new ACOffCommand(ac));

        System.out.println("Pressing buttons:");
        remote.pressButton(0); // Light ON
        remote.pressButton(2); // AC ON
        remote.pressButton(1); // Light OFF

        // Multi-level undo - the naive version could only remember one action
        System.out.println("\nUndoing, one step at a time:");
        remote.pressUndo(); // reverses Light OFF -> Light ON
        remote.pressUndo(); // reverses AC ON    -> AC OFF
        remote.pressUndo(); // reverses Light ON -> Light OFF
        remote.pressUndo(); // history empty

        // Re-assigning a button at runtime: slot 0 is no longer "Light ON"
        System.out.println("\n[slot 0 re-programmed as Night Mode]");
        List<Command> nightMode = new ArrayList<>();
        nightMode.add(new LightOffCommand(light));
        nightMode.add(new ACOnCommand(ac));
        remote.setCommand(0, new MacroCommand(nightMode));

        remote.pressButton(0); // runs both actions as one

        System.out.println("\nUndoing night mode (reverse order):");
        remote.pressUndo(); // AC OFF first, then Light ON
    }
}
