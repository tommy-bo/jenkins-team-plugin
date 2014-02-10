package org.jenkinsci.plugins.teams;

import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.teams.Teams.TeamPluginDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean(defaultVisibility = 999)
public class TeamUserProperty extends UserProperty {
	private String teamName;

	public TeamUserProperty() {
	}

	@DataBoundConstructor
	public TeamUserProperty(String teamName) {
		this.teamName = teamName;
	}

	@Exported
	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	@Override
	public TeamUserDescription getDescriptor() {
		return (TeamUserDescription) super.getDescriptor();
	}

	@Extension
	public static final class TeamUserDescription extends UserPropertyDescriptor {

    public TeamUserDescription() {
        super(TeamUserProperty.class);
    }

		@Override
		public UserProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			return req.bindJSON(TeamUserProperty.class, formData);
		}

		@Override
		public UserProperty newInstance(User user) {
			return new TeamUserProperty();
		}

		@Override
		public String getDisplayName() {
			return "TeamUserProperty";
		}

		public ListBoxModel doFillTeamNameItems() {
			return ((TeamPluginDescriptor) Jenkins.getInstance().getDescriptorOrDie(Teams.class)).doFillResponsibleTeamItems();
		}
	}
}
