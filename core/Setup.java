/**
 * 
 */
package emf_toolcenter.plugin.backend.core;

import java.io.IOException;
/**
 * @author DEP7EC
 *
 */
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import emf_lib.interpreter.Interpreter;
import emf_toolcenter.plugin.backend.interpreter.strategy.impl.InitialProjectImportInterpreterStrategy;
import emf_toolcenter.plugin.backend.interpreter.strategy.impl.InterpretableProjectWrapper;
import emf_toolcenter.plugin.backend.interpreter.strategy.impl.XmlInterpreterStrategy;
import emf_toolcenter.plugin.backend.model.Project;
import emf_toolcenter.plugin.backend.util.PathProcessor;
import emf_toolcenter.plugin.backend.util.ProjectInformation;

/**
 * @author dep7ec
 *
 */
public class Setup {
	
	private PathProcessor processor;
	private Set <Project> projectList;
	private Interpreter transmuter;
	private Project curProject;
	private Path curPath;
	
	/**
	 * 
	 */
	public Setup(){
		this.transmuter = null;
		this.processor = new PathProcessor();
		this.projectList = new LinkedHashSet<>();
	}
	
	/**
	 * 
	 * @return
	 * @throws NullPointerException
	 */
	public Project projectToView() throws NullPointerException{
		Project recentProject = null;
		//Ausgewähltes Projekt im Explorer
		ProjectInformation curSelection = this.processor.getProjectInformation();
		Path recentPath = curSelection.getProjectPath();
		//Ausgewähltes Projekt ist das gleiche
		if(recentPath.equals(this.curPath)){
			recentProject = curProject;
		}
		//Ausgewähltes Projekt wurde bereits benutzt
		else if((this.getCurProject(recentPath)) != null){
			recentProject = this.getCurProject(recentPath);
		}
		//Ausgewähltes Projekt ist neu in dieser Sitzung
		else{
			//Ausgewähltes Projekt wurde schon mal geöffnet und hat eine ini
			if(recentPath.resolve(".settings/emf_toolcenter.prefs").toFile().exists()){
				this.transmuter = new Interpreter(new XmlInterpreterStrategy());
				recentProject = (Project) this.transmuter.interpret(
						recentPath.resolve(".settings/emf_toolcenter.prefs")).getData();
			}
			//Ausgewähltes Projekt ist ganz neu
			else{
				try {
					this.transmuter = new Interpreter(
							new InitialProjectImportInterpreterStrategy(curSelection.getProjectName()));
					recentProject = (Project) this.transmuter.interpret(recentPath).getData();
					recentProject.setChangeFlag();
					this.exportProjectChanges(recentProject);
				} catch (NullPointerException nex) {
					recentProject = new Project(curSelection.getProjectName(), recentPath, 
							recentPath.resolve(".settings/emf_toolcenter.prefs"));
				}
			}
			this.projectList.add(recentProject);
		}
		if (recentProject != null){
			this.curProject = recentProject;
			this.curPath = recentProject.getProjectPath();
		}
		return (Project) recentProject.clone();
	}
	
	/**
	 * 
	 * @param projectPath
	 * @return
	 */
	private Project getCurProject(Path projectPath){
		Project recent = null;
		Iterator <Project> projectItr = this.projectList.iterator();
		while(projectItr.hasNext()){
			Project actItr = projectItr.next();
			if(actItr.getProjectPath().equals(projectPath)) {
				recent = actItr;
			}
		}
		return recent;
	}
	
	/**
	 * 
	 * @param callerID
	 */
	public void executeTool(Integer callerID){
		this.curProject.callTool(callerID);
	}
	
	/**
	 * 
	 * @param remove
	 */
	private void removeProject(Project remove){
		Iterator <Project> project = this.projectList.iterator();
		while(project.hasNext()){
			curProject = project.next();
			if(curProject.equals(remove)){
				this.projectList.remove(curProject);
				this.curProject = null;
				this.curPath = null;
			}
		}
	}
	
	/**
	 * 
	 * @param delete
	 */
	public void deleteProject(Project delete){
		this.removeProject(delete);
		try {
			this.processor.deleteFile(delete.getPreferencesPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param substitition
	 */
	public void substituteProject(Project substitition){
		this.removeProject(substitition);
		this.exportProjectChanges(substitition);
	}
	
	/**
	 * 
	 * @param export
	 */
	public void exportProjectChanges(Project export){
		this.transmuter = new Interpreter(new XmlInterpreterStrategy());
		if(export == null){
		Iterator <Project> project = this.projectList.iterator();
			while(project.hasNext()){
				if(project.next().getStatus() != null)
					transmuter.interpret(new InterpretableProjectWrapper(project.next()));
			}
		}else{
			transmuter.interpret(new InterpretableProjectWrapper(export));
		}
	}
}
