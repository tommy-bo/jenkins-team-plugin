/*
 * The MIT License
 *
 * Copyright 2014 Tommy Bo <tommy.bo@visma.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.teams;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.JobProperty;
import hudson.model.Result;
import hudson.model.Saveable;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.model.ViewGroup;
import hudson.util.ListBoxModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

import hudson.model.ItemGroup;
import hudson.model.ModifiableItemGroup;
import static hudson.model.Result.ABORTED;

/**
 *
 * @author Tommy Bo <tommy.bo@visma.com>
 */
public class TeamView extends View implements Saveable {

    private String teamName;

    @DataBoundConstructor
    public TeamView(String name, ViewGroup owner) {
        super(name, owner);
    }

    @Exported
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public Collection<TopLevelItem> getItems() {
        List<TopLevelItem> result = new ArrayList<>();
        List<TopLevelItem> allItems = Jenkins.getInstance().getAllItems(TopLevelItem.class);
        for (TopLevelItem item : allItems) {
            if (teamIsResponsibleFor(item) || teammemberBrokeIt(item)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public boolean contains(TopLevelItem item) {
        return teamIsResponsibleFor(item);
    }

    @Override
    protected void submit(StaplerRequest req) throws IOException, ServletException, Descriptor.FormException {
        JSONObject json = req.getSubmittedForm();
        this.setTeamName(json.getString("teamName"));

    }

    @Override
    public Item doCreateItem(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        ItemGroup<? extends TopLevelItem> ig = getOwnerItemGroup();
        if (ig instanceof ModifiableItemGroup) {
            TopLevelItem item = ((ModifiableItemGroup<? extends TopLevelItem>) ig).doCreateItem(req, rsp);
            if (item instanceof AbstractProject) {
                ((AbstractProject) item).addProperty(new TeamJobProperty(teamName));
                item.save();
            }
            return item;
        }
        return null;
    }

    private boolean teamIsResponsibleFor(Item item) {
        if (item instanceof AbstractProject) {
            JobProperty property = ((AbstractProject) item).getProperty(TeamJobProperty.class);
            return property != null && teamName.equals(((TeamJobProperty) property).getResponsibleTeam());
        }
        return false;
    }

    private boolean teammemberBrokeIt(TopLevelItem item) {
        if (item instanceof AbstractProject) {
            AbstractBuild lastBuild = ((AbstractProject) item).getLastBuild();
            while (lastBuild != null && (lastBuild.isBuilding() || lastBuild.getResult() == ABORTED)) {
                lastBuild = lastBuild.getPreviousBuild();
            }
            if (lastBuild != null && lastBuild.getResult() != null && lastBuild.getResult().isWorseThan(Result.SUCCESS)) {
                for (User user : (Set<User>) lastBuild.getCulprits()) {
                    final TeamUserProperty teamProperty = user.getProperty(TeamUserProperty.class);
                    if (notNull(teamProperty) && notNull(teamProperty.getTeamName()) && teamProperty.getTeamName().equals(teamName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean notNull(Object object) {
        return object != null;
    }

    @Extension
    public static final class DescriptorImpl extends ViewDescriptor {

        @Override
        public String getDisplayName() {
            return "TeamView";
        }

        public ListBoxModel doFillTeamNameItems() {
            return ((TeamJobProperty.TeamPluginDescriptor) Jenkins.getInstance().getDescriptorOrDie(TeamJobProperty.class)).doFillResponsibleTeamItems();
        }
    }
}
