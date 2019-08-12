package greencity.utils;

import java.util.Collection;
import java.util.List;

/**
 * The class provides an interface for mapping entity objects to dto objects and vice versa.
 */
public interface ModelMapperUtil {

    /**
     * Maps a source object to a new destination object.
     *
     * @param srcObj    object to map from
     * @param destClass type to map to
     * @param <S>       type of the source class
     * @param <D>       type of the destination class
     * @return an object of the {@code D} type
     */
    <S, D> D map(final S srcObj, Class<D> destClass);

    /**
     * Maps a source object to a given destination object.
     *
     * @param srcObj  object to map from
     * @param destObj object to map to
     * @param <S>     type of the source class
     * @param <D>     type of the destination class
     * @return the {@code destObj} object mapped from {@code srcObj}
     */
    <S, D> D map(final S srcObj, D destObj);

    /**
     * Maps a collection of source objects to a new list of destination objects.
     *
     * @param srcList   list of objects to map from
     * @param destClass type to map to
     * @param <S>       type of the source class
     * @param <D>       type of the destination class
     * @return a list of the {@code D} objects
     */
    <S, D> List<D> mapAll(final Collection<S> srcList, Class<D> destClass);
}
