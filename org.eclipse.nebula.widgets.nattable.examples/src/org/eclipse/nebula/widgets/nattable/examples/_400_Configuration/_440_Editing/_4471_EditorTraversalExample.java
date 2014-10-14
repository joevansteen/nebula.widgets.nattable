/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.examples._400_Configuration._440_Editing;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.ExtendedReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowIdAccessor;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDateDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.DateCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.examples.AbstractNatExample;
import org.eclipse.nebula.widgets.nattable.examples.data.person.Person;
import org.eclipse.nebula.widgets.nattable.examples.data.person.Person.Gender;
import org.eclipse.nebula.widgets.nattable.examples.data.person.PersonService;
import org.eclipse.nebula.widgets.nattable.examples.runner.StandaloneNatExampleRunner;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.RowOnlySelectionBindings;
import org.eclipse.nebula.widgets.nattable.selection.config.RowOnlySelectionConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class _4471_EditorTraversalExample extends AbstractNatExample {

	public static void main(String[] args) throws Exception {
		StandaloneNatExampleRunner.run(1024, 400, new _4471_EditorTraversalExample());
	}
	
	@Override
	public String getDescription() {
		return
				"";
	}
	
	public static String COLUMN_ONE_LABEL = "ColumnOneLabel";
	public static String COLUMN_TWO_LABEL = "ColumnTwoLabel";
	public static String COLUMN_THREE_LABEL = "ColumnThreeLabel";
	public static String COLUMN_FOUR_LABEL = "ColumnFourLabel";
	public static String COLUMN_FIVE_LABEL = "ColumnFiveLabel";
	
	@Override
	public Control createExampleControl(Composite parent) {
		//property names of the Person class
		String[] propertyNames = {"firstName", "lastName", "married", "gender", "birthday"};

		//mapping from property to label, needed for column header labels
		Map<String, String> propertyToLabelMap = new HashMap<String, String>();
		propertyToLabelMap.put("firstName", "Firstname");
		propertyToLabelMap.put("lastName", "Lastname");
		propertyToLabelMap.put("married", "Married");
		propertyToLabelMap.put("gender", "Gender");
		propertyToLabelMap.put("birthday", "Birthday");
		
		IRowDataProvider<Person> bodyDataProvider = new ListDataProvider<Person>(
				PersonService.getPersons(10), 
				new ExtendedReflectiveColumnPropertyAccessor<Person>(propertyNames));
		
		DefaultGridLayer gridLayer = new DefaultGridLayer(
				bodyDataProvider, new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap));
		
		
		SelectionLayer selectionLayer = gridLayer.getBodyLayer().getSelectionLayer();
		
		//use a RowSelectionModel that will perform row selections and is able to identify a row via unique ID
		selectionLayer.setSelectionModel(new RowSelectionModel<Person>(selectionLayer, bodyDataProvider, 
				new IRowIdAccessor<Person>() {

			@Override
			public Serializable getRowId(Person rowObject) {
				return rowObject.getId();
			}
			
		}));
		
		//register different selection move command handler that always moves by row
		selectionLayer.addConfiguration(new RowOnlySelectionConfiguration<Person>());
		
		//register selection bindings that will perform row selections instead of cell selections
		//registering the bindings on a layer that is above the SelectionLayer will consume the
		//commands before they are handled by the SelectionLayer
		gridLayer.getBodyLayer().getViewportLayer().addConfiguration(new RowOnlySelectionBindings());

		
		final DataLayer bodyDataLayer = (DataLayer) gridLayer.getBodyDataLayer();

		final ColumnOverrideLabelAccumulator columnLabelAccumulator = new ColumnOverrideLabelAccumulator(bodyDataLayer);
		bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);
		registerColumnLabels(columnLabelAccumulator);
		
		final NatTable natTable = new NatTable(parent, gridLayer, false);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration() {
			{
				cellPainter = new LineBorderDecorator(new TextPainter(false, true, true));
			}
			
			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				super.configureRegistry(configRegistry);
				
				Style cellStyle = new Style();
				cellStyle.setAttributeValue(CellStyleAttributes.FONT, 
						GUIHelper.getFont(new FontData("Verdana", 14, SWT.BOLD | SWT.ITALIC))); //$NON-NLS-1$);
				
				configRegistry.registerConfigAttribute(
						CellConfigAttributes.CELL_STYLE, 
						cellStyle, 
						DisplayMode.SELECT);

			}
		});
		natTable.addConfiguration(new EditorConfiguration());
		natTable.configure();
		
		new NatTableContentTooltip(natTable, GridRegion.BODY);
		
		return natTable;
	}
	
	
	private void registerColumnLabels(ColumnOverrideLabelAccumulator columnLabelAccumulator) {
		columnLabelAccumulator.registerColumnOverrides(0, COLUMN_ONE_LABEL);
		columnLabelAccumulator.registerColumnOverrides(1, COLUMN_TWO_LABEL);
		columnLabelAccumulator.registerColumnOverrides(2, COLUMN_THREE_LABEL);
		columnLabelAccumulator.registerColumnOverrides(3, COLUMN_FOUR_LABEL);
		columnLabelAccumulator.registerColumnOverrides(4, COLUMN_FIVE_LABEL);
	}
	
	
	class EditorConfiguration extends AbstractRegistryConfiguration  {
		
		@Override
		public void configureRegistry(IConfigRegistry configRegistry) {
			configRegistry.registerConfigAttribute(
					EditConfigAttributes.CELL_EDITABLE_RULE, 
					IEditableRule.ALWAYS_EDITABLE);
			
			//configure to open the adjacent editor after commit
			configRegistry.registerConfigAttribute(
					EditConfigAttributes.OPEN_ADJACENT_EDITOR,
					Boolean.TRUE);

			registerEditors(configRegistry);
		}
		
		private void registerEditors(IConfigRegistry configRegistry) {
			registerColumnTwoTextEditor(configRegistry);
			registerColumnThreeCheckbox(configRegistry);
			registerColumnFourCheckbox(configRegistry);
			registerColumnFiveDateEditor(configRegistry);
		}
		
		private void registerColumnTwoTextEditor(IConfigRegistry configRegistry) {
			//register a TextCellEditor for column two that commits on key up/down
			//moves the selection after commit by enter
			configRegistry.registerConfigAttribute(
					EditConfigAttributes.CELL_EDITOR, 
					new TextCellEditor(true, true), 
					DisplayMode.NORMAL, 
					_4471_EditorTraversalExample.COLUMN_TWO_LABEL);
		}
		
		/**
		 * The following will register a default CheckBoxCellEditor for the column that
		 * carries the married information.
		 * <p>
		 * To register a CheckBoxCellEditor, you need to 
		 * <ol>
		 * <li>Register the editor</li>
		 * <li>Register the painter corresponding to that editor</li>
		 * <li>Register the needed converter</li>
		 * </ol>
		 * @param configRegistry
		 */
		private void registerColumnThreeCheckbox(IConfigRegistry configRegistry) {
			//register a CheckBoxCellEditor for column three
			configRegistry.registerConfigAttribute(
					EditConfigAttributes.CELL_EDITOR, 
					new CheckBoxCellEditor(), 
					DisplayMode.EDIT, 
					_4471_EditorTraversalExample.COLUMN_THREE_LABEL);
			
			//if you want to use the CheckBoxCellEditor, you should also consider
			//using the corresponding CheckBoxPainter to show the content like a
			//checkbox in your NatTable
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.CELL_PAINTER, 
					new CheckBoxPainter(), 
					DisplayMode.NORMAL, 
					_4471_EditorTraversalExample.COLUMN_THREE_LABEL);
			
			//using a CheckBoxCellEditor also needs a Boolean conversion to work correctly
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.DISPLAY_CONVERTER, 
					new DefaultBooleanDisplayConverter(), 
					DisplayMode.NORMAL, 
					_4471_EditorTraversalExample.COLUMN_THREE_LABEL);
		}
		
		/**
		 * The following will register a CheckBoxCellEditor with custom icons for the column that
		 * carries the gender information. As a Gender is not a Boolean, there need to be a special
		 * converter registered. Note that such a converter needs to create a Boolean display value
		 * and create the canonical value out of a Boolean value again.
		 * <p>
		 * To register a CheckBoxCellEditor, you need to 
		 * <ol>
		 * <li>Register the editor</li>
		 * <li>Register the painter corresponding to that editor</li>
		 * <li>Register the needed converter</li>
		 * </ol>
		 * @param configRegistry
		 */
		private void registerColumnFourCheckbox(IConfigRegistry configRegistry) {
			//register a CheckBoxCellEditor for column four
			configRegistry.registerConfigAttribute(
					EditConfigAttributes.CELL_EDITOR, 
					new CheckBoxCellEditor(), 
					DisplayMode.EDIT, 
					_4471_EditorTraversalExample.COLUMN_FOUR_LABEL);
			
			//if you want to use the CheckBoxCellEditor, you should also consider
			//using the corresponding CheckBoxPainter to show the content like a
			//checkbox in your NatTable
			//in this case we use different icons to show how this works
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.CELL_PAINTER, 
					new CheckBoxPainter(GUIHelper.getImage("arrow_up"), GUIHelper.getImage("arrow_down")), 
					DisplayMode.NORMAL, 
					_4471_EditorTraversalExample.COLUMN_FOUR_LABEL);
			
			//using a CheckBoxCellEditor also needs a Boolean conversion to work correctly
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.DISPLAY_CONVERTER, 
					getGenderBooleanConverter(), 
					DisplayMode.NORMAL, 
					_4471_EditorTraversalExample.COLUMN_FOUR_LABEL);
		}
		
		private void registerColumnFiveDateEditor(IConfigRegistry configRegistry) {
			configRegistry.registerConfigAttribute(
					EditConfigAttributes.CELL_EDITOR, 
					new DateCellEditor(), 
					DisplayMode.EDIT, 
					_4471_EditorTraversalExample.COLUMN_FIVE_LABEL);
			
			DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
			String pattern       = ((SimpleDateFormat)formatter).toPattern();
			
			//using a DateCellEditor also needs a Date conversion to work correctly
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.DISPLAY_CONVERTER, 
					new DefaultDateDisplayConverter(pattern), 
					DisplayMode.NORMAL, 
					_4471_EditorTraversalExample.COLUMN_FIVE_LABEL);
		}
		
		/**
		 * @return Returns a simple converter for the gender of a Person.
		 * 			{@link Gender#MALE} will be interpreted as <code>true</code>
		 * 			while {@link Gender#FEMALE} will be interpreted as <code>false</code>
		 */
		private IDisplayConverter getGenderBooleanConverter() {
			return new DisplayConverter() {
				
				@Override
				public Object canonicalToDisplayValue(Object canonicalValue) {
					if (canonicalValue instanceof Gender) {
						return ((Gender)canonicalValue) == Gender.MALE;
					}
					return null;
				}
				
				@Override
				public Object displayToCanonicalValue(Object displayValue) {
					Boolean displayBoolean = Boolean.valueOf(displayValue.toString());
					return displayBoolean ? Gender.MALE : Gender.FEMALE;
				}
				
			};
		}
	}
}


