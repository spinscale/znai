import React, {Component} from 'react'

import './Presentation.css'

const defaultScaleRatio = 2

class Presentation extends Component {
    constructor(props) {
        super(props)

        this.state = {currentSlideIdx: 0, scaleRatio: defaultScaleRatio}

        this.keyDownHandler = this.keyDownHandler.bind(this)
        this.onClose = this.onClose.bind(this)
    }

    render() {
        const {presentationRegistry} = this.props
        const {currentSlideIdx, isAppeared, scaleRatio} = this.state
        const slideAreaStyle = {transform: 'scale(' + scaleRatio + ')'}

        const slideClassName = "slide-area" + (isAppeared ? " appeared": "")
        const component = presentationRegistry.componentToRender(currentSlideIdx)
        // const showNextButton = currentSlideIdx >= presentationRegistry.numberOfSlides - 1 TODO

        return (<div className="presentation">
            <div className="header">
                <div className="info">
                    <div className="company-title"></div>
                    <div className="slide-title"></div>
                </div>

                <div className="controls">
                    <div className="presentation-close-icon glyphicon glyphicon-remove" onClick={this.onClose}/>
                </div>
            </div>

            <div className={slideClassName} ref={(n) => this.slideAreaDom = n}>
                <div ref={(n) => this.componentDom = n} style={slideAreaStyle}>
                    {component}
                </div>
            </div>

            <div className="footer">
                <div className="slide-number">
                    {currentSlideIdx + 1}/{presentationRegistry.numberOfSlides}
                </div>
            </div>
        </div>)
    }

    componentDidMount() {
        document.addEventListener('keydown', this.keyDownHandler)
        this.updateSlide()
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevState.currentSlideIdx !== this.state.currentSlideIdx) {
            this.updateSlide()
        }
    }

    componentWillUnmount() {
        document.removeEventListener('keydown', this.keyDownHandler)
    }

    componentWillReceiveProps(props) {
        const {presentationRegistry} = this.props
        if (this.scrollToLastWithinPage && presentationRegistry !== props.presentationRegistry) {
            this.scrollToLastWithinPage = false
            this.setState({currentSlideIdx: props.presentationRegistry.numberOfSlides - 1, scaleRatio: defaultScaleRatio})
        }
    }

    updateSlide() {
        this.updateScaleRatio()
        this.updateAnimation()
    }

    updateScaleRatio() {
        const width = this.componentDom.offsetWidth
        const height = this.componentDom.offsetHeight

        const widthRatio = this.slideAreaDom.offsetWidth / width
        const heightRatio = this.slideAreaDom.offsetHeight / height

        const scaleRatio = Math.min(widthRatio, heightRatio, 2.5)

        this.setState({scaleRatio})
    }

    updateAnimation() {
        setTimeout(() => {
            this.setState({isAppeared: true})
        }, 0)
    }

    keyDownHandler(e) {
        const {presentationRegistry, onNextPage, onPrevPage} = this.props
        const {currentSlideIdx} = this.state
        let newSlideIdx = currentSlideIdx

        if (e.key === 'ArrowRight') {
            newSlideIdx += 1
        } else if (e.key === 'ArrowLeft') {
            newSlideIdx -= 1
        } else if (e.key === 'Escape') {
            this.onClose()
            return
        }

        if (newSlideIdx < 0) {
            this.scrollToLastWithinPage = true
            onPrevPage()
        } else if (newSlideIdx >= presentationRegistry.numberOfSlides) {
            this.setState({currentSlideIdx: 0, scaleRatio: defaultScaleRatio, isAppeared: false})
            onNextPage()
        } else if (newSlideIdx !== currentSlideIdx) {
            this.setState({currentSlideIdx: newSlideIdx, scaleRatio: defaultScaleRatio, isAppeared: false})
        }
    }

    onClose() {
        const {onClose} = this.props
        if (onClose) {
            onClose()
        }
    }
}

export default Presentation

