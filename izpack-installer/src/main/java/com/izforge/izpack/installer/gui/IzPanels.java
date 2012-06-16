package com.izforge.izpack.installer.gui;

import java.util.List;

import com.izforge.izpack.api.container.Container;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.panel.AbstractPanels;


/**
 * Implementation of {@link AbstractPanels} for {@link IzPanel}.
 *
 * @author Tim Anderson
 */
public class IzPanels extends AbstractPanels<IzPanelView, IzPanel>
{
    /**
     * The installation data.
     */
    private final GUIInstallData installData;

    /**
     * The container to add {@link IzPanel}s to.
     */
    private final Container container;

    /**
     * The listener to notify of events.
     */
    private IzPanelsListener listener;

    /**
     * Determines if the current panel switch is navigating backwards or forwards.
     */
    private boolean isBack = false;

    /**
     * Constructs a {@code IzPanels}.
     *
     * @param panels      the panels
     * @param container   the container to register {@link IzPanel}s with
     * @param installData the installation data
     */
    public IzPanels(List<IzPanelView> panels, Container container, GUIInstallData installData)
    {
        super(panels, installData.getVariables());
        this.container = container;
        this.installData = installData;
    }

    /**
     * Initialises the {@link IzPanel} instances.
     */
    public void initialise()
    {
        for (IzPanelView panel : getPanelViews())
        {
            // need to defer creation of the IzPanel until after the InstallerFrame is constructed
            IzPanel view = panel.getView();
            installData.getPanels().add(view);
            String panelId = panel.getPanelId();
            if (panelId == null)
            {
                panelId = view.getClass().getName();
            }
            container.addComponent(panelId, view);
        }
    }

    /**
     * Sets the listener to notify of events.
     *
     * @param listener the listener. May be {@code null}
     */
    public void setListener(IzPanelsListener listener)
    {
        this.listener = listener;
    }

    /**
     * Determines if the next panel may be navigated to.
     *
     * @param enable if {@code true}, enable navigation, otherwise disable it
     */
    @Override
    public void setNextEnabled(boolean enable)
    {
        super.setNextEnabled(enable);
        if (listener != null)
        {
            listener.setNextEnabled(enable);
        }
    }

    /**
     * Determines if the previous panel may be navigated to.
     *
     * @param enable if {@code true}, enable navigation, otherwise disable it
     */
    @Override
    public void setPreviousEnabled(boolean enable)
    {
        super.setPreviousEnabled(enable);
        if (listener != null)
        {
            listener.setPreviousEnabled(enable);
        }
    }

    /**
     * Determines if the current panel switch is navigating backwards or forwards.
     *
     * @return {@code true} if the current panel switch is navigating backwards, {@code false} if navigating forwads
     *         or no panel switch is in progress
     */
    public boolean isBack()
    {
        return isBack;
    }

    /**
     * Switches panels.
     *
     * @param newPanel the panel to switch to
     * @param oldPanel the panel to switch from, or {@code null} if there was no prior panel
     * @return {@code true} if the switch was successful
     */
    @Override
    protected boolean switchPanel(IzPanelView newPanel, IzPanelView oldPanel)
    {
        boolean result = false;
        try
        {
            isBack = oldPanel != null && newPanel.getIndex() < oldPanel.getIndex();
            if (listener != null)
            {
                listener.switchPanel(newPanel, oldPanel);
                result = true;
            }
        }
        finally
        {
            isBack = false;
        }
        return result;
    }

}