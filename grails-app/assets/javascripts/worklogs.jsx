var App = React.createClass({

    getInitialState: function () {
        return {
            worklogs: []
        }
    },

    componentDidMount: function () {
        console.log('start');
        // Is there a React-y way to avoid rebinding `this`? fat arrow?
        var th = this;
        this.serverRequest =
            axios.get(this.props.source)
                .then(function (result) {
                    console.log(result);
                    th.setState({
                        worklogs: result.data
                    });
                })
    },

    componentWillUnmount: function () {
        this.serverRequest.abort();
    },

    render: function () {
        return (
            <div>
                <h1>Jobs!</h1>
                {/* Don't have an ID to use for the key, URL work ok? */}
                {this.state.worklogs.map(function (worklog) {
                    return (
                        <div key={worklog.url} className="job">
                            <a href={worklog.url}>
                                {worklog.company_name}
                                is looking for a
                                {worklog.term}
                                {worklog.title}
                            </a>
                        </div>
                    );
                })}
            </div>
        )
    }
});

React.render(<App source="/reportData/2"/>, document.querySelector("#root"));