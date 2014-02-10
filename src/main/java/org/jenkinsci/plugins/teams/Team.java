package org.jenkinsci.plugins.teams;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class Team extends AbstractDescribableImpl<Team> {
	private String teamName;

	@DataBoundConstructor
	public Team(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	@Extension
	public static class TeamDescriptor extends Descriptor<Team> {

		@Override
		public String getDisplayName() {
			return "";
		}
	}
}
