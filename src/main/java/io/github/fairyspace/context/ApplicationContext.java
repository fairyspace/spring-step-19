package io.github.fairyspace.context;

import io.github.fairyspace.beans.core.io.ResourceLoader;
import io.github.fairyspace.beans.factory.HierarchicalBeanFactory;
import io.github.fairyspace.beans.factory.ListableBeanFactory;

public interface ApplicationContext extends ListableBeanFactory  , HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher{

}
