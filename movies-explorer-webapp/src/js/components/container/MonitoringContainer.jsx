import React, { Component } from "react";
import { Button } from '@material-ui/core';

import Config from '../../config/Config';

class MonitoringContainer extends Component {

    constructor() {
        super();

        this.changeIframeToAllDashboardUrl = this.changeIframeToAllDashboardUrl.bind(this);
    }

    changeIframeToAllDashboardUrl() {
        document.getElementById('dashboard').src = Config.getGrafanaAllDashboardsUrl()
    }

    render() {
        return (<div> <br />
           The following public Grafana dashboard exposes some of the main technical metrics of every component of the system in addition to the "business" metrics of the system, 
            here the entities (movies, tags and ratings) imported and subsequently added by users. These metrics are scraped by a Prometheus instance running within the cluster.
            <br /><br />
            You'll find more complete operational dashboards by cliking here:  
            <Button variant="contained" size="small" style={{fontWeight: "bolder"}} onClick={this.changeIframeToAllDashboardUrl}>All dashboards</Button>
            {/* <a href="https://github.com/barelyThinkingBagOfWater/k8/tree/master/grafana/provisioning/dashboards">dashboards</a>. */}
            <br /><br />
            This system is running on Google Kubernetes Engine (region us-central1 on two nodes). Every component runs in a single instance mode with no autoscaling as resources don't grow on trees.
            <br /><br />
            <iframe id="dashboard" className="dashboard" src={Config.getGrafanaPublicDashboardUrl()} />
        </div>)
    }
}

export default MonitoringContainer