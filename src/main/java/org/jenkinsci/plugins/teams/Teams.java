package org.jenkinsci.plugins.teams;

import com.google.common.base.Optional;
import hudson.Launcher;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Teams extends JobProperty<AbstractProject<?,?>> {

	private final String responsibleTeam;

	@DataBoundConstructor
	public Teams(String responsibleTeam) {
		this.responsibleTeam = responsibleTeam;
	}

	public String getResponsibleTeam() {
		return responsibleTeam;
	}

	@Override
	public TeamPluginDescriptor getDescriptor() {
		return (TeamPluginDescriptor) super.getDescriptor();
	}

	@Extension
	public static final class TeamPluginDescriptor extends JobPropertyDescriptor {

		private List<Team> teams;

		public TeamPluginDescriptor() {
			super(Teams.class);
			load();
		}
		public FormValidation doCheckTeamName(@QueryParameter String value)
						throws IOException, ServletException {
			if (value.length() == 0) {
				return FormValidation.error("Please provide a teamname");
			}
			return FormValidation.ok();
		}

		@Override
		public boolean isApplicable(Class<? extends Job> jobType) {
			return true;
		}

		public String getDisplayName() {
			return "Teamster";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			req.bindJSON(this, formData);
			save();
			return true;
		}

		public Optional<Team> getTeamByName(String teamName) {
			for (Team team : getTeams()) {
				if(team.getTeamName().equals(teamName))
					return Optional.of(team);
			}
			return Optional.absent();
		}

		public List<Team> getTeams() {
			if(teams == null) {
				teams = new ArrayList<Team>();
			}
			return teams;
		}

		public void setTeams(List<Team> teams) {
			this.teams = teams;
		}

		public ListBoxModel doFillResponsibleTeamItems() {
			ListBoxModel list = new ListBoxModel();
			for (Team team : getTeams()) {
				list.add(team.getTeamName());
			}
			return list;
		}
	}
}
