import React from "react";
import { Link } from "react-router-dom";
import { AppBar, Toolbar, Button } from '@material-ui/core';

const HeaderBar = () => (
  <div className="header">
          <AppBar style={{ backgroundColor: "inherit" }} position="static">
            <Toolbar>
              <div className="header-logo">
                <Link className="link" to="/" style={{paddingLeft: "0px"}}>
                  <img src="./logo.png"
                    alt="Let's find a movie to watch!" />
                </Link></div>
              <div className="header-toolbar">
                <Link className="link" to="/">
                  <Button style={navButtonStyle}>Home</Button>
                </Link>
                <Link className="link" to="/monitoring">
                  <Button style={navButtonStyle}>Monitoring</Button>
                </Link>
                <Link className="link" to="/sources">
                  <Button style={navButtonStyle}>Sources</Button>
                </Link>
              </div>
            </Toolbar>
          </AppBar>
  </div>
)

const navButtonStyle = {
  color: "white",
  fontSize: "19px",
  fontFamily: "Yeon Sung, cursive",
  fontWeight: "bold",
}

export default HeaderBar

