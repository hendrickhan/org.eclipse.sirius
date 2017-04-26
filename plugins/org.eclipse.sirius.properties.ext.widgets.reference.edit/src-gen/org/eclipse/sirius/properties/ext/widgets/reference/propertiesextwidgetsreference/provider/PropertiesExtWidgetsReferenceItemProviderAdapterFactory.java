/**
 * Copyright (c) 2016 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *
 */
package org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ChildCreationExtenderManager;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.sirius.properties.AbstractContainerDescription;
import org.eclipse.sirius.properties.AbstractDynamicMappingIfDescription;
import org.eclipse.sirius.properties.AbstractGroupDescription;
import org.eclipse.sirius.properties.Category;
import org.eclipse.sirius.properties.PropertiesPackage;
import org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.PropertiesExtWidgetsReferenceFactory;
import org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.PropertiesExtWidgetsReferencePackage;
import org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.util.PropertiesExtWidgetsReferenceAdapterFactory;
import org.eclipse.sirius.properties.util.PropertiesSwitch;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers. The adapters generated by this
 * factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}. The adapters
 * also support Eclipse property sheets. Note that most of the adapters are shared among multiple instances. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class PropertiesExtWidgetsReferenceItemProviderAdapterFactory extends PropertiesExtWidgetsReferenceAdapterFactory
        implements ComposeableAdapterFactory, IChangeNotifier, IDisposable, IChildCreationExtender {
    /**
     * This keeps track of the root adapter factory that delegates to this adapter factory. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    protected ComposedAdapterFactory parentAdapterFactory;

    /**
     * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    protected IChangeNotifier changeNotifier = new ChangeNotifier();

    /**
     * This helps manage the child creation extenders. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ChildCreationExtenderManager childCreationExtenderManager = new ChildCreationExtenderManager(PropertiesextwidgetsreferenceEditPlugin.INSTANCE,
            PropertiesExtWidgetsReferencePackage.eNS_URI);

    /**
     * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected Collection<Object> supportedTypes = new ArrayList<Object>();

    /**
     * This constructs an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public PropertiesExtWidgetsReferenceItemProviderAdapterFactory() {
        supportedTypes.add(IEditingDomainItemProvider.class);
        supportedTypes.add(IStructuredItemContentProvider.class);
        supportedTypes.add(ITreeItemContentProvider.class);
        supportedTypes.add(IItemLabelProvider.class);
        supportedTypes.add(IItemPropertySource.class);
        supportedTypes.add(IItemStyledLabelProvider.class);
    }

    /**
     * This keeps track of the one adapter used for all
     * {@link org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.ExtReferenceDescription}
     * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ExtReferenceDescriptionItemProvider extReferenceDescriptionItemProvider;

    /**
     * This creates an adapter for a
     * {@link org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.ExtReferenceDescription}.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Adapter createExtReferenceDescriptionAdapter() {
        if (extReferenceDescriptionItemProvider == null) {
            extReferenceDescriptionItemProvider = new ExtReferenceDescriptionItemProvider(this);
        }

        return extReferenceDescriptionItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all
     * {@link org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.ExtReferenceWidgetStyle}
     * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ExtReferenceWidgetStyleItemProvider extReferenceWidgetStyleItemProvider;

    /**
     * This creates an adapter for a
     * {@link org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.ExtReferenceWidgetStyle}.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Adapter createExtReferenceWidgetStyleAdapter() {
        if (extReferenceWidgetStyleItemProvider == null) {
            extReferenceWidgetStyleItemProvider = new ExtReferenceWidgetStyleItemProvider(this);
        }

        return extReferenceWidgetStyleItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all
     * {@link org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.ExtReferenceWidgetConditionalStyle}
     * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ExtReferenceWidgetConditionalStyleItemProvider extReferenceWidgetConditionalStyleItemProvider;

    /**
     * This creates an adapter for a
     * {@link org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.ExtReferenceWidgetConditionalStyle}.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Adapter createExtReferenceWidgetConditionalStyleAdapter() {
        if (extReferenceWidgetConditionalStyleItemProvider == null) {
            extReferenceWidgetConditionalStyleItemProvider = new ExtReferenceWidgetConditionalStyleItemProvider(this);
        }

        return extReferenceWidgetConditionalStyleItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all
     * {@link org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.ExtReferenceOverrideDescription}
     * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ExtReferenceOverrideDescriptionItemProvider extReferenceOverrideDescriptionItemProvider;

    /**
     * This creates an adapter for a
     * {@link org.eclipse.sirius.properties.ext.widgets.reference.propertiesextwidgetsreference.ExtReferenceOverrideDescription}.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Adapter createExtReferenceOverrideDescriptionAdapter() {
        if (extReferenceOverrideDescriptionItemProvider == null) {
            extReferenceOverrideDescriptionItemProvider = new ExtReferenceOverrideDescriptionItemProvider(this);
        }

        return extReferenceOverrideDescriptionItemProvider;
    }

    /**
     * This returns the root adapter factory that contains this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ComposeableAdapterFactory getRootAdapterFactory() {
        return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
    }

    /**
     * This sets the composed adapter factory that contains this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
        this.parentAdapterFactory = parentAdapterFactory;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object type) {
        return supportedTypes.contains(type) || super.isFactoryForType(type);
    }

    /**
     * This implementation substitutes the factory itself as the key for the adapter. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Adapter adapt(Notifier notifier, Object type) {
        return super.adapt(notifier, this);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object adapt(Object object, Object type) {
        if (isFactoryForType(type)) {
            Object adapter = super.adapt(object, type);
            if (!(type instanceof Class<?>) || (((Class<?>) type).isInstance(adapter))) {
                return adapter;
            }
        }

        return null;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List<IChildCreationExtender> getChildCreationExtenders() {
        return childCreationExtenderManager.getChildCreationExtenders();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain) {
        return childCreationExtenderManager.getNewChildDescriptors(object, editingDomain);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return childCreationExtenderManager;
    }

    /**
     * This adds a listener. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void addListener(INotifyChangedListener notifyChangedListener) {
        changeNotifier.addListener(notifyChangedListener);
    }

    /**
     * This removes a listener. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void removeListener(INotifyChangedListener notifyChangedListener) {
        changeNotifier.removeListener(notifyChangedListener);
    }

    /**
     * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void fireNotifyChanged(Notification notification) {
        changeNotifier.fireNotifyChanged(notification);

        if (parentAdapterFactory != null) {
            parentAdapterFactory.fireNotifyChanged(notification);
        }
    }

    /**
     * This disposes all of the item providers created by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void dispose() {
        if (extReferenceDescriptionItemProvider != null) {
            extReferenceDescriptionItemProvider.dispose();
        }
        if (extReferenceWidgetStyleItemProvider != null) {
            extReferenceWidgetStyleItemProvider.dispose();
        }
        if (extReferenceWidgetConditionalStyleItemProvider != null) {
            extReferenceWidgetConditionalStyleItemProvider.dispose();
        }
        if (extReferenceOverrideDescriptionItemProvider != null) {
            extReferenceOverrideDescriptionItemProvider.dispose();
        }
    }

    /**
     * A child creation extender for the {@link PropertiesPackage}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static class PropertiesChildCreationExtender implements IChildCreationExtender {
        /**
         * The switch for creating child descriptors specific to each extended class. <!-- begin-user-doc --> <!--
         * end-user-doc -->
         * 
         * @generated
         */
        protected static class CreationSwitch extends PropertiesSwitch<Object> {
            /**
             * The child descriptors being populated. <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected List<Object> newChildDescriptors;

            /**
             * The domain in which to create the children. <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected EditingDomain editingDomain;

            /**
             * Creates the a switch for populating child descriptors in the given domain. <!-- begin-user-doc --> <!--
             * end-user-doc -->
             * 
             * @generated
             */
            CreationSwitch(List<Object> newChildDescriptors, EditingDomain editingDomain) {
                this.newChildDescriptors = newChildDescriptors;
                this.editingDomain = editingDomain;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            @Override
            public Object caseCategory(Category object) {
                newChildDescriptors.add(createChildParameter(PropertiesPackage.Literals.CATEGORY__OVERRIDES, PropertiesExtWidgetsReferenceFactory.eINSTANCE.createExtReferenceOverrideDescription()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            @Override
            public Object caseAbstractGroupDescription(AbstractGroupDescription object) {
                newChildDescriptors
                        .add(createChildParameter(PropertiesPackage.Literals.ABSTRACT_GROUP_DESCRIPTION__CONTROLS, PropertiesExtWidgetsReferenceFactory.eINSTANCE.createExtReferenceDescription()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            @Override
            public Object caseAbstractContainerDescription(AbstractContainerDescription object) {
                newChildDescriptors
                        .add(createChildParameter(PropertiesPackage.Literals.ABSTRACT_CONTAINER_DESCRIPTION__CONTROLS, PropertiesExtWidgetsReferenceFactory.eINSTANCE.createExtReferenceDescription()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            @Override
            public Object caseAbstractDynamicMappingIfDescription(AbstractDynamicMappingIfDescription object) {
                newChildDescriptors.add(createChildParameter(PropertiesPackage.Literals.ABSTRACT_DYNAMIC_MAPPING_IF_DESCRIPTION__WIDGET,
                        PropertiesExtWidgetsReferenceFactory.eINSTANCE.createExtReferenceDescription()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected CommandParameter createChildParameter(Object feature, Object child) {
                return new CommandParameter(null, feature, child);
            }

        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public Collection<Object> getNewChildDescriptors(Object object, EditingDomain editingDomain) {
            ArrayList<Object> result = new ArrayList<Object>();
            new CreationSwitch(result, editingDomain).doSwitch((EObject) object);
            return result;
        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public ResourceLocator getResourceLocator() {
            return PropertiesextwidgetsreferenceEditPlugin.INSTANCE;
        }
    }

}
