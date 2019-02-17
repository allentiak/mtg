import React, {PureComponent} from 'react';
import {connect} from 'react-redux'
import Card from '../Card'
import {get} from 'lodash'

class OpponentLandArea extends PureComponent {
  render() {
    return (
      <div id="opponent-land-area">
        {this.props.cards.map((cardInstance) => <Card key={cardInstance.id} name={cardInstance.card.name} />)}
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    cards: get(state, 'player.battlefield', [])
  }
}

const mapDispatchToProps = dispatch => {
  return {
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(OpponentLandArea)