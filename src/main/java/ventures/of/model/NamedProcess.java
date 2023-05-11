package ventures.of.model;

public class NamedProcess {
    public String program;
    public Process process;

    public NamedProcess(String program, Process process) {
        this.process = process;
        this.program = program;
    }
}
