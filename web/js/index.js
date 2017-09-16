var EMPTY = React.createClass({
    getInitialState: function () {
        return {
            value: "",
            flag: true,
            row: 0,
            size: 8,
            data: null,
            source:null,
        };
    },
    handleChange: function (e) {
        this.setState({value: e.target.value})
    },
    handlePage:function (page, pageSize){
    	this.setState({data:this.state.source.slice((page-1)*pageSize,page*pageSize)})
    },
    handleDataSource:function(data){
    	var url = "/solrsearch/solr/query.do";
    	  CALLJSON(url,data,function (e) {
              if(e.code==1){
                  var dataSource = [];
                  for(var i =0;i<e.rows.length;i++){
                      var ob = {key:i,data:{site:e.rows[i]["site"],url:e.rows[i]["url"],name:e.rows[i]["title"],label:e.rows[i]["label"],content:e.rows[i]["content"],type:(e.rows[i]["title"]).split(".")[1]}};
                      dataSource.push(ob);
                  }
                  this.setState({row:e.total,data:dataSource.slice(0,this.state.size),flag: false,source:dataSource})
              }
          }.bind(this),function () {
              this.setState({flag: false});
          }.bind(this))
    },
    handleSearch: function () {
    	var keyword = (this.state.value).replace(/^[\s　]+|[\s　]+$/g, "");
        if (keyword != "") {
        	if(keyword.length<2){
        		this.message("warning","warning","请至少输入2个关键字");
        		return;
        	}
            var data = {params:{keyword:keyword}}
              this.handleDataSource(data);
        } else {
            this.setState({flag: true})
        }
    },
    fileIcon: function (type) {
        if (type == "pdf") {
            return "icon anticon icon-pdffile1";
        } else if (type == "doc" || type == "docx") {
            return "icon anticon icon-wordfile1";
        } else if (type == "xlsx" || type == "xls") {
            return "icon anticon icon-exclefile1";
        } else return "icon anticon icon-filetext1";
    },
    message:function(type,message,desc){
    	antd.notification[type]({
    	    message: message,
    	    description: desc,
    	    duration:3,
    	  });
    },
    handleLable:function(value,key){
    	console.log(value.search(key))
    	if(value.search(key)!=-1){
    		return true
    	}else{
    		return false
    		}
    },
    render: function () {
    	var empty = <div style={{width: "100%", "padding-top": "150px"}}>
            <div style={{margin: "0 auto", width: "38%"}}>
                <antd.Input.Group compact>
                    <antd.Input size="large"
                                onPressEnter={this.handleSearch}
                                value={this.state.value}
                                placeholder="Search"
                                onChange={this.handleChange}
                                style={{width: '85%'}}/>
                    <antd.Button size="large"
                                 onClick={this.handleSearch}
                                 style={{width: '14%'}}
                                 type="primary">搜索
                    </antd.Button>
                </antd.Input.Group>
            </div>
        </div>
        var columns = [{
            title: '',
            dataIndex: 'data',
            width: '66%',
            render: function (text, record, index) {
            	var labs = (this.state.data[index]['data']['label']).substring(1,(this.state.data[index]['data']['label'].length-1)).split(/[,，]/g);
                var lab = "";
            	for(var i =0 ;i<labs.length;i++){
                	lab +="<span class='tab'>"+labs[i]+"</span>";
                }
                //var pic ="javascript:pic('"+this.state.data[index]['data']['site']+this.state.data[index]['data']['name'].replace(/<em>/,"").replace("</em>","")+"')";
            	var filekey ="/solrsearch/solr/download.do?fileId="+encodeURI(encodeURI(this.state.data[index]['data']['url']));
                return (<div>
                    <antd.Row>
                        <antd.Col>
                            <span style={{'font-size': '22px'}}
                                  className={this.fileIcon(this.state.data[index]['data']['type'])}/>
                            <a href={filekey} 
                               style={{
                                'margin-left': '15px',
                                'font-size': '22px',
                                color: '#00c'
                            }} dangerouslySetInnerHTML={{__html:this.state.data[index]['data']['name']}}/>
                        </antd.Col>
                    </antd.Row>
                    <antd.Row>
                        <antd.Col>
                            <div style={{'padding-top': '8px'}}
                                 className="light-font content" dangerouslySetInnerHTML={{__html: this.state.data[index]['data']['content']}}/>
                        </antd.Col>
                    </antd.Row>
                    <antd.Row align="bottom">
                        <antd.Col span={22}>
                            <div style={{'padding-top': '10px'}} dangerouslySetInnerHTML={{__html:lab}} />
                        </antd.Col>
                        <antd.Col span={2}>
                            <a style={{'float':'right','font-size': '22px', 'padding-top': '10px'}}
                               className="icon anticon icon-download" href={filekey}></a>
                        </antd.Col>
                    </antd.Row>
                </div>)
            }.bind(this)
        }]
        var footer = '';
        if (this.state.row > this.state.size) {
            footer = < antd.Layout.Footer>
                <div style={{'padding-left': '188px', width: '60%'}}>
                    <antd.Pagination
                        size="large"
                        total={this.state.row}
                        onChange={this.handlePage}
                        pageSize={this.state.size}
                       current={this.state.page}
                    />
                </div>
            </antd.Layout.Footer>
        }
        var notEmpty = <antd.Layout style={{background: '#fff'}}>
            <antd.Layout.Header style={{zIndex: 999, position: 'fixed', width: '100%', background: '#f7f7f7'}}>
                <div style={{margin: "1px 80px", width: "38%"}}>
                    <antd.Input.Group compact>
                        <antd.Input size="large"
                                    onPressEnter={this.handleSearch}
                                    value={this.state.value}
                                    onChange={this.handleChange}
                                    style={{width: '85%'}}/>
                        <antd.Button size="large"
                                     onClick={this.handleSearch}
                                     style={{width: '14%'}}
                                     type="primary">搜索
                        </antd.Button>
                    </antd.Input.Group>
                </div>
            </antd.Layout.Header>
            <antd.Layout.Content style={{padding: '0 100px', marginTop: 64}}>
                <div style={{width: '62%'}}>
                    <antd.Table pagination={false}
                                showHeader={false}
                                dataSource={this.state.data}
                                columns={columns}/>
                </div>
            </antd.Layout.Content>
            {footer}
        </antd.Layout>
        return (
            this.state.flag ? empty : notEmpty
        );
    }
});

ReactDOM.render(
    <EMPTY/>,
    document.getElementById('index')
);
