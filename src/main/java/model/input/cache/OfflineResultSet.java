package model.input.cache;

public class OfflineResultSet extends AbstractResultSet {

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
    }
}
