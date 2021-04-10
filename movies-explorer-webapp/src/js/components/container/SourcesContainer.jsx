import React, { Component } from "react";

class SourcesContainer extends Component {

    render() {
        return (
            <div> <br />
                <div>This project runs on a Kubernetes cluster (here GKE) and is composed by different reactive applications/services coded in different languages that interact in a CQRS architecture. 
                    Thanks to (at least horizontal, here turned off) autoscaling this system should be able to handle millions of requests simultaneously.</div>
                <br />
                <div>The sources for each component of this project and more can be found on my Github account : <a href="https://github.com/barelyThinkingBagOfWater">Github</a></div>
                <br />
                <div>Not every component has been deployed on GKE to spare resources, such as an EFK stack or a Jenkins + Sonar instance. The internal and monitoring namespaces that include what would run
                    within a company run on one node, the dmz one (default namespace) runs on another.
                    Here is a diagram of the currently deployed architecture:</div>
                <br />
                <img src="./archi.png" alt="Architecture of the system" />
            </div>
        )
    }
}

export default SourcesContainer