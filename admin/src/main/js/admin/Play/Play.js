import React, {Component} from 'react'
import {connect} from 'react-redux'
import get from 'lodash/get'
import AuthHelper from '../Auth/AuthHelper'
import './play.scss'
import ApiClient from '../utils/ApiClient'
import Loader from '../Common/Loader'

class Play extends Component {
  constructor(props) {
    super(props)
    this.handlePlay = this.handlePlay.bind(this)
    this.toggle = this.toggle.bind(this)
    this.state = {colors: []}
  }

  handlePlay(event) {
    event.preventDefault()
    this.setState({errorMessage: null})

    if (this.state.colors.length === 0) {
      this.setState({errorMessage: 'You need to select at least one color.'})
      return
    }

    const playerOptions = {
      randomColors: this.state.colors
    }

    const request = {
      gameType: 'UNLIMITED',
      playerOptions: JSON.stringify(playerOptions)
    }

    this.setState({loading: true})
    ApiClient.post('/game', request)
      .then(r => {
        this.setState({loading: false})
        if (r.gameId > 0) {
          window.location.href = this.props.matagGameUrl + '/ui/game/' + r.gameId
        } else {
          this.setState({errorMessage: r.errorMessage})
        }
      })
  }

  isSelected(color) {
    return this.state.colors.indexOf(color) > -1
  }

  toggle(color) {
    const colors = this.state.colors
    if (this.isSelected(color)) {
      colors.splice(colors.indexOf(color), 1)
      this.setState({colors: colors})

    } else {
      this.setState({colors: [...colors, color]})
    }
  }

  displayErrorMessage() {
    if (this.state.errorMessage) {
      return <p className='error'>{this.state.errorMessage}</p>
    }
  }

  displayLoader() {
    if (this.state.loading) {
      return <Loader center/>
    }
  }

  render() {
    return (
      <section id='play'>
        <h2>Play</h2>
        <form className='matag-form' onSubmit={this.handlePlay}>
          <input type='hidden' name='session' value={AuthHelper.getToken()} />
          <p>Choose which colors you want to play:</p>
          <ul>
            <li>
              <input type='checkbox' id='color-white' name='white' checked={this.isSelected('white')} onChange={() => this.toggle('white')}/>
              <label htmlFor='color-white'><img src='/img/symbols/WHITE.png' alt='white'/>White</label>
            </li>
            <li>
              <input type='checkbox' id='color-blue' name='blue' checked={this.isSelected('blue')} onChange={() => this.toggle('blue')}/>
              <label htmlFor='color-blue'><img src='/img/symbols/BLUE.png' alt='blue'/>Blue</label>
            </li>
            <li>
              <input type='checkbox' id='color-black' name='black' checked={this.isSelected('black')} onChange={() => this.toggle('black')}/>
              <label htmlFor='color-black'><img src='/img/symbols/BLACK.png' alt='black'/>Black</label>
            </li>
            <li>
              <input type='checkbox' id='color-red' name='red' checked={this.isSelected('red')} onChange={() => this.toggle('red')}/>
              <label htmlFor='color-red'><img src='/img/symbols/RED.png' alt='red'/>Red</label>
            </li>
            <li>
              <input type='checkbox' id='color-green' name='green' checked={this.isSelected('green')} onChange={() => this.toggle('green')}/>
              <label htmlFor='color-green'><img src='/img/symbols/GREEN.png' alt='green'/>Green</label>
            </li>
          </ul>
          { this.displayErrorMessage() }
          { this.displayLoader() }
          <div className='grid three-columns'>
            <div/>
            <input type='submit' id='play-button' value='Play'/>
          </div>
        </form>
      </section>
    )
  }
}

const mapStateToProps = state => {
  return {
    matagGameUrl: get(state, 'config.matagGameUrl', '')
  }
}

const mapDispatchToProps = dispatch => {
  return {}
}

export default connect(mapStateToProps, mapDispatchToProps)(Play)