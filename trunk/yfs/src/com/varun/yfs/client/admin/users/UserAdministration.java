package com.varun.yfs.client.admin.users;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.varun.yfs.client.admin.rpc.StoreLoader;
import com.varun.yfs.client.admin.rpc.StoreLoaderAsync;
import com.varun.yfs.client.common.RpcStatusEnum;
import com.varun.yfs.client.images.YfsImageBundle;
import com.varun.yfs.dto.UserChapterPermissionsDTO;
import com.varun.yfs.dto.UserDTO;
import com.varun.yfs.dto.UserProjectPermissionsDTO;

public class UserAdministration extends LayoutContainer
{
	private final StoreLoaderAsync storeLoader = GWT.create(StoreLoader.class);
	private EditorGrid<UserDTO> editorGridUser;
	private final ContentPanel gridPanel = new ContentPanel();

	private final ContentPanel userDetailsViewHolder = new ContentPanel();
	private final TextField<String> txtfldUsrName = new TextField<String>();
	private final TextField<String> txtfldPassword = new TextField<String>();
	private SimpleComboBox<String> userRole = new SimpleComboBox<String>();

	private String curAdminEntity = "Default";
	private ModelData currentModelData = new BaseModelData();
	private boolean isAdd;

	public UserAdministration()
	{
		setHeight("700px");
	}

	final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>()
	{
		public void handleEvent(MessageBoxEvent ce)
		{
		}
	};

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		setLayout(new FitLayout());

		ContentPanel mainPanel = new ContentPanel();
		mainPanel.setHeaderVisible(true);
		mainPanel.setHeading("Administration");
		mainPanel.setLayout(new FitLayout());
		mainPanel.setTopComponent(getUserAdminToolbar());

		TableLayout tl_lp = new TableLayout(1);
		tl_lp.setCellPadding(5);
		mainPanel.setLayout(tl_lp);

		TableData td_gridPanel = new TableData();
		td_gridPanel.setRowspan(2);
		mainPanel.setScrollMode(Scroll.AUTOY);
		mainPanel.add(gridPanel, td_gridPanel);

		add(mainPanel, new FitData(5));

		buildUserNameAdminPanel();

		buildBasicUserInfoPanel();

		buildPermissionsGrid();

		TableData td_lstViewHolder = new TableData();
		td_lstViewHolder.setHorizontalAlign(HorizontalAlignment.LEFT);
		td_lstViewHolder.setRowspan(2);
		td_lstViewHolder.setPadding(5);
		td_lstViewHolder.setMargin(5);
		mainPanel.add(userDetailsViewHolder, td_lstViewHolder);
		userDetailsViewHolder.setWidth("382px");
	}

	private ToolBar getUserAdminToolbar()
	{
		ToolBar toolBar = new ToolBar();
		
		Button add = new Button("Add");
		add.setIcon(AbstractImagePrototype.create(YfsImageBundle.INSTANCE.addButtonIcon()));
		add.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{

				txtfldUsrName.clear();
				txtfldPassword.clear();

				userDetailsViewHolder.setVisible(true);
				userDetailsViewHolder.focus();

				isAdd = true;
			}
		});
		toolBar.add(add);

		Button remove = new Button("Remove");
		remove.setIcon(AbstractImagePrototype.create(YfsImageBundle.INSTANCE.deleteButtonIcon()));
		remove.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				editorGridUser.stopEditing();
				UserDTO selectedItem = editorGridUser.getSelectionModel().getSelectedItem();
				if (selectedItem != null)
				{
					selectedItem.set("deleted", "Y");
					List<UserDTO> lstModels = editorGridUser.getStore().getModels();
					editorGridUser.getStore().remove(selectedItem);
					editorGridUser.mask("Removing Entry...");
					savePage(lstModels);
				}
			}
		});
		toolBar.add(remove);
		
		return toolBar;
	}

	private void buildBasicUserInfoPanel()
	{
		userDetailsViewHolder.setHeading("User Details");
		userDetailsViewHolder.setVisible(false);

		FormPanel frmpanelUserBasic = new FormPanel();
		frmpanelUserBasic.setHeaderVisible(false);
		frmpanelUserBasic.setCollapsible(false);
		frmpanelUserBasic.setBorders(false);
		frmpanelUserBasic.setFrame(false);

		txtfldUsrName.setMaxLength(255);
		txtfldUsrName.setName("userName");
		txtfldUsrName.setFieldLabel("User Name");

		txtfldPassword.setPassword(true);
		txtfldPassword.setName("userPassword");
		txtfldPassword.setMaxLength(255);
		txtfldPassword.setFieldLabel("Password");

		userRole.setTriggerAction(TriggerAction.ALL);
		userRole.setFieldLabel("Role");
		userRole.add("Administrator");
		userRole.add("Administrator - Chapter");
		userRole.add("Area Co-Ordinator");

		frmpanelUserBasic.add(txtfldUsrName, new FormData("80%"));
		frmpanelUserBasic.add(txtfldPassword, new FormData("80%"));
		frmpanelUserBasic.add(userRole, new FormData("80%"));

		userDetailsViewHolder.setButtonAlign(HorizontalAlignment.CENTER);
		userDetailsViewHolder.addButton(new Button("Reset", new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				reinitPage(curAdminEntity);
				userDetailsViewHolder.setVisible(false);
			}
		}));

		userDetailsViewHolder.addButton(new Button("Save", new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				userDetailsViewHolder.setVisible(false);
				List<UserDTO> models = editorGridUser.getStore().getModels();

				if (isAdd)
				{
					UserDTO modelData = new UserDTO();
					models.add(modelData);
					modelData.setName(txtfldUsrName.getValue());
					modelData.setPassword(txtfldPassword.getValue());
					modelData.setRole(userRole.getSimpleValue());
				} else
				{
					UserDTO modelData = editorGridUser.getSelectionModel().getSelectedItem();
					if (modelData != null)
					{
						modelData.setName(txtfldUsrName.getValue());
						modelData.setPassword(txtfldPassword.getValue());
						modelData.setRole(userRole.getSimpleValue());
					}
				}
				savePage(models);
			}
		}));

		userRole.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se)
			{
				if (se.getSelectedItem().equals("Administrator"))
				{

				} else if (se.getSelectedItem().equals("Administrator - Chapter"))
				{

				} else if (se.getSelectedItem().equals("Area Co-Ordinator"))
				{
				}

			}
		});

		// userDetailsViewHolder.setSize("300px", "600px");
		userDetailsViewHolder.add(frmpanelUserBasic, new FitData(5));
	}

	private void buildUserNameAdminPanel()
	{
		gridPanel.setLayout(new FitLayout());
		gridPanel.setHeading(curAdminEntity);
		gridPanel.setSize("300px", "200px");

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig clmncnfgNewColumn = new ColumnConfig("name", "Name", 150);
		configs.add(clmncnfgNewColumn);

		ListStore<ModelData> editorGridStore = new ListStore<ModelData>();

		editorGridUser = new EditorGrid<UserDTO>(editorGridStore, new ColumnModel(configs));
		editorGridUser.setHideHeaders(true);
		editorGridUser.setSelectionModel(new GridSelectionModel<UserDTO>());
		editorGridUser.setLoadMask(true);
		editorGridUser.setAutoExpandColumn("name");
		editorGridUser.mask("Loading...");
		editorGridUser.setAutoWidth(true);
		editorGridUser.setClicksToEdit(EditorGrid.ClicksToEdit.ONE);
		gridPanel.add(editorGridUser);

		editorGridUser.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<ModelData>>()
		{
			@SuppressWarnings("unchecked")
			public void handleEvent(SelectionChangedEvent<ModelData> be)
			{
				List<ModelData> selection = be.getSelection();
				if (selection.size() > 0)
				{
					txtfldUsrName.clear();
					txtfldPassword.clear();
					userRole.clearSelections();

					ModelData modelData = selection.get(0);
					txtfldUsrName.setValue(modelData.get("name").toString());
					txtfldPassword.setValue(modelData.get("password").toString());
					Object role = modelData.get("role");
					if (role != null && userRole.findModel(role.toString()) != null)
					{
						userRole.setValue(userRole.findModel(role.toString()));
					}

					userDetailsViewHolder.setVisible(true);
					userDetailsViewHolder.focus();
				}
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			private <E> void updateSelections(List<? extends ModelData> modelData, CheckBoxListView view)
			{
				for (ModelData modelData1 : modelData)
				{
					int idx = view.getStore().getModels().indexOf(modelData1);
					if (idx >= 0)
					{
						view.setChecked(view.getStore().getAt(idx), true);
					}
				}
			}
		});
	}

	private void buildPermissionsGrid()
	{

		ContentPanel cpChapterGrid = new ContentPanel();
		cpChapterGrid.setLayout(new FitLayout());
		cpChapterGrid.setHeaderVisible(false);

		List<ColumnConfig> configsChapter = new ArrayList<ColumnConfig>();
		ColumnConfig clmncnfgNewColumn_1 = new ColumnConfig("chapterName", "Chapter", 120);
		final SimpleComboBox<String> field = new SimpleComboBox<String>();
		field.setTriggerAction(TriggerAction.ALL);
		CellEditor editor = new CellEditor(field)
		{
			@Override
			public Object preProcessValue(Object value)
			{
				if (value == null)
				{
					return value;
				}
				return field.findModel(value.toString());
			}

			@Override
			public Object postProcessValue(Object value)
			{
				if (value == null)
				{
					return value;
				}
				return ((ModelData) value).get("value");
			}
		};
		// field.add(lstValues);
		clmncnfgNewColumn_1.setEditor(editor);
		configsChapter.add(clmncnfgNewColumn_1);

		CheckColumnConfig checkColumn = new CheckColumnConfig("read", "Read?", 55);
		CellEditor checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		configsChapter.add(checkColumn);

		checkColumn = new CheckColumnConfig("write", "Write?", 55);
		checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		configsChapter.add(checkColumn);

		checkColumn = new CheckColumnConfig("delete", "Delete?", 55);
		checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		configsChapter.add(checkColumn);

		final EditorGrid<UserChapterPermissionsDTO> editorGridChapter = new EditorGrid<UserChapterPermissionsDTO>(new ListStore<UserChapterPermissionsDTO>(), new ColumnModel(configsChapter));
		editorGridChapter.setHeight(200);
		editorGridChapter.setLoadMask(true);
		editorGridChapter.setColumnLines(true);
		cpChapterGrid.add(editorGridChapter);
		editorGridChapter.setBorders(true);

		ToolBar toolBarChapterPerm = new ToolBar();
		Button addChapterPerm = new Button("Add");
		addChapterPerm.setIcon(AbstractImagePrototype.create(YfsImageBundle.INSTANCE.addButtonIcon()));
		addChapterPerm.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				UserChapterPermissionsDTO plant = new UserChapterPermissionsDTO();

				editorGridChapter.getStore().add(plant);
				editorGridChapter.startEditing(editorGridChapter.getStore().getCount() - 1, 0);
			}
		});
		toolBarChapterPerm.add(addChapterPerm);

		Button removeChapterPerm = new Button("Remove");
		removeChapterPerm.setIcon(AbstractImagePrototype.create(YfsImageBundle.INSTANCE.deleteButtonIcon()));
		removeChapterPerm.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				editorGridChapter.stopEditing();
				UserChapterPermissionsDTO selectedItem = editorGridChapter.getSelectionModel().getSelectedItem();
				if (selectedItem != null)
				{
					selectedItem.set("deleted", "Y");

					List<UserChapterPermissionsDTO> lstModels = editorGridChapter.getStore().getModels();
					UserDTO curUser = editorGridUser.getSelectionModel().getSelectedItem();
					curUser.setChapterPermissions(lstModels);

					editorGridChapter.getStore().remove(selectedItem);
				}
			}
		});
		toolBarChapterPerm.add(removeChapterPerm);
		cpChapterGrid.setTopComponent(toolBarChapterPerm);

		ContentPanel cpProjectGrid = new ContentPanel();
		cpProjectGrid.setLayout(new FitLayout());
		cpProjectGrid.setHeaderVisible(false);

		List<ColumnConfig> configsProjectGrid = new ArrayList<ColumnConfig>();
		ColumnConfig clmncnfgProjectName = new ColumnConfig("projectName", "Project", 120);
		final SimpleComboBox<String> fieldChapter = new SimpleComboBox<String>();
		fieldChapter.setTriggerAction(TriggerAction.ALL);
		editor = new CellEditor(fieldChapter)
		{
			@Override
			public Object preProcessValue(Object value)
			{
				if (value == null)
				{
					return value;
				}
				return fieldChapter.findModel(value.toString());
			}

			@Override
			public Object postProcessValue(Object value)
			{
				if (value == null)
				{
					return value;
				}
				return ((ModelData) value).get("value");
			}
		};
		// field.add(lstValues);
		clmncnfgProjectName.setEditor(editor);
		configsProjectGrid.add(clmncnfgProjectName);

		checkColumn = new CheckColumnConfig("read", "Read?", 55);
		checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		configsProjectGrid.add(checkColumn);

		checkColumn = new CheckColumnConfig("write", "Write?", 55);
		checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		configsProjectGrid.add(checkColumn);

		checkColumn = new CheckColumnConfig("delete", "Delete?", 55);
		checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		configsProjectGrid.add(checkColumn);

		final EditorGrid<UserProjectPermissionsDTO> editorGridProject = new EditorGrid<UserProjectPermissionsDTO>(new ListStore<UserProjectPermissionsDTO>(), new ColumnModel(configsProjectGrid));
		editorGridProject.setHeight(200);
		editorGridProject.setLoadMask(true);
		editorGridProject.setColumnLines(true);
		cpProjectGrid.add(editorGridProject);
		editorGridProject.setBorders(true);

		ToolBar toolBarProjectPerm = new ToolBar();
		Button addProjectPerm = new Button("Add");
		addProjectPerm.setIcon(AbstractImagePrototype.create(YfsImageBundle.INSTANCE.addButtonIcon()));
		addProjectPerm.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				UserProjectPermissionsDTO plant = new UserProjectPermissionsDTO();

				editorGridProject.getStore().add(plant);
				editorGridProject.startEditing(editorGridProject.getStore().getCount() - 1, 0);
			}
		});
		toolBarProjectPerm.add(addProjectPerm);

		Button removeProjectPerm = new Button("Remove");
		removeProjectPerm.setIcon(AbstractImagePrototype.create(YfsImageBundle.INSTANCE.deleteButtonIcon()));
		removeProjectPerm.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				editorGridChapter.stopEditing();
				UserProjectPermissionsDTO selectedItem = editorGridProject.getSelectionModel().getSelectedItem();
				if (selectedItem != null)
				{
					selectedItem.set("deleted", "Y");

					List<UserProjectPermissionsDTO> lstModels = editorGridProject.getStore().getModels();
					UserDTO curUser = editorGridUser.getSelectionModel().getSelectedItem();
					curUser.setProjectPermissions(lstModels);

					editorGridProject.getStore().remove(selectedItem);
				}
			}
		});
		toolBarProjectPerm.add(removeProjectPerm);
		cpProjectGrid.setTopComponent(toolBarProjectPerm);

		userDetailsViewHolder.add(cpChapterGrid, new FitData(5));
		userDetailsViewHolder.add(cpProjectGrid, new FitData(5));
	}

	public void savePage(final List<UserDTO> lstModels)
	{
		ModelData modelData = new BaseModelData();
		modelData.set("users", lstModels);
		storeLoader.saveModel(curAdminEntity, modelData, new AsyncCallback<RpcStatusEnum>()
		{
			@Override
			public void onSuccess(RpcStatusEnum result)
			{
				reinitPage(curAdminEntity);
				if (result.compareTo(RpcStatusEnum.FAILURE) == 0)
				{
					MessageBox.alert("Alert", "Error encountered while saving", l);
				}
			}

			@Override
			public void onFailure(Throwable caught)
			{
				editorGridUser.unmask();
				MessageBox.alert("Alert", "Error encountered while saving", l);
			}
		});
	}

	public void reinitPage(String entityName)
	{
		this.curAdminEntity = entityName;
		gridPanel.setHeading(entityName);
		storeLoader.getModel(entityName, new AsyncCallback<ModelData>()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ModelData result)
			{
				currentModelData = result;
				editorGridUser.getStore().removeAll();
				editorGridUser.getStore().add((List<UserDTO>) currentModelData.get("users"));
				editorGridUser.getStore().commitChanges();
				editorGridUser.unmask();
				editorGridUser.setAutoWidth(true);
			}

			@Override
			public void onFailure(Throwable caught)
			{
				editorGridUser.unmask();
				MessageBox.alert("Alert", "Error encountered while loading", l);
			}
		});

	}

}
