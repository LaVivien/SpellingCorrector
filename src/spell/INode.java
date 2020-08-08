package spell;


public interface INode {
    int getValue();
    void incrementValue();
    INode[] getChildren();
}