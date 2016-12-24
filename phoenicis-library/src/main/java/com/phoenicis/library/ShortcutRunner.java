package com.phoenicis.library;

import com.phoenicis.library.dto.ShortcutDTO;
import com.playonlinux.scripts.interpreter.InteractiveScriptSession;
import com.playonlinux.scripts.interpreter.ScriptInterpreter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.List;

public class ShortcutRunner {
    private final ScriptInterpreter scriptInterpreter;

    public ShortcutRunner(ScriptInterpreter scriptInterpreter) {
        this.scriptInterpreter = scriptInterpreter;
    }

    public void run(ShortcutDTO shortcutDTO, List<String> arguments) {
        final InteractiveScriptSession interactiveScriptSession = scriptInterpreter.createInteractiveSession();

        interactiveScriptSession.eval("include([\"Functions\", \"Shortcuts\", \"Reader\"]);",
                ignored -> interactiveScriptSession.eval(
                        "new ShortcutReader()",
                        output -> {
                            final ScriptObjectMirror shortcutReader = (ScriptObjectMirror) output;
                            shortcutReader.callMember("of", shortcutDTO.getScript());
                            shortcutReader.callMember("run", arguments);
                        },
                        this::throwError
                ),
                this::throwError
        );


    }

    private void throwError(Exception e) {
        throw new IllegalStateException(e);
    }
}
