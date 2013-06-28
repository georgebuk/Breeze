﻿describe('util', function () {
    'use strict';
    var u; // util

    it('app module should be present', function () {
        expect(angular.module('app')).toBeDefined();
    });

    beforeEach(module('app'));

    beforeEach(inject(function (util) {
        u = util;
    }));

    it('should be created', function () {
        expect(u).toBeTruthy();
    });

    it('should have function members', function () {
        expect(u.filterById).toBeTruthy();
        expect(u.filterByName).toBeTruthy();
        expect(u.filterByType).toBeTruthy();
        expect(u.getSaveErrorMessages).toBeTruthy();
        expect(u.getEntityValidationErrMsgs).toBeTruthy();
        expect(u.segmentArray).toBeTruthy();
    });

    it('should segmentArray 7 into 3', function () {
        //[1,2,3,4,5,6,7], 3) -> [[1,4,7],[2,5],[3,6]]
        var arr = [1, 2, 3, 4, 5, 6, 7];
        var segs = u.segmentArray(arr, 3);
        expect(segs.length).toEqual(3);
        expect(segs[0].length).toEqual(3);
        expect(segs[1].length).toEqual(2);
        expect(segs[2].length).toEqual(2);
        expect(segs[0][0]).toEqual(1);
        expect(segs[0][1]).toEqual(4);
        expect(segs[0][2]).toEqual(7);
        expect(segs[1][0]).toEqual(2);
        expect(segs[1][1]).toEqual(5);
        expect(segs[2][0]).toEqual(3);
        expect(segs[2][1]).toEqual(6);
    });

    it('should segmentArray 7 into 2', function () {
        //[1,2,3,4,5,6,7], 2) -> [[1,3,5,7],[2,4,6]]
        var arr = [1, 2, 3, 4, 5, 6, 7];
        var segs = u.segmentArray(arr, 2);
        expect(segs.length).toEqual(2);
        expect(segs[0].length).toEqual(4);
        expect(segs[1].length).toEqual(3);
        expect(segs[0][0]).toEqual(1);
        expect(segs[0][1]).toEqual(3);
        expect(segs[0][2]).toEqual(5);
        expect(segs[0][3]).toEqual(7);
        expect(segs[1][0]).toEqual(2);
        expect(segs[1][1]).toEqual(4);
        expect(segs[1][2]).toEqual(6);
    });

    it('should segmentArray 6 into 2', function () {
        //[1,2,3,4,5,6], 2) -> [[1,3,5],[2,4,6]]
        var arr = [1, 2, 3, 4, 5, 6];
        var segs = u.segmentArray(arr, 2);
        expect(segs.length).toEqual(2);
        expect(segs[0].length).toEqual(3);
        expect(segs[1].length).toEqual(3);
        expect(segs[0][0]).toEqual(1);
        expect(segs[0][1]).toEqual(3);
        expect(segs[0][2]).toEqual(5);
        expect(segs[1][0]).toEqual(2);
        expect(segs[1][1]).toEqual(4);
        expect(segs[1][2]).toEqual(6);
    });

    it('should groupArray 6 into 3 key,values groups', function () {
        //arr -> [{key:'a', values:[{ name: 'a', val: 'a1' }, { name: 'a', val: 'a2' },  { name: 'a', val: 'a3' }]},
        //        {key:'b', values:[{ name: 'b', val: 'b1' }]},
        //        {key:'c', values:[{ name: 'c', val: 'c2' }, { name: 'c', val: 'c1' }]}]
        var arr = [
            { name: 'a', val: 'a1' },
            { name: 'b', val: 'b1' },
            { name: 'c', val: 'c2' },
            { name: 'c', val: 'c1' },
            { name: 'a', val: 'a2' },
            { name: 'a', val: 'a3' },
        ];
        var keyfn = function (o) { return o.name; };
        var groups = u.groupArray(arr, keyfn);
        expect(groups.length).toEqual(3);
        expect(groups[0].key).toEqual('a');
        expect(groups[1].key).toEqual('b');
        expect(groups[2].key).toEqual('c');
        expect(groups[0].values.length).toEqual(3);
        expect(groups[1].values.length).toEqual(1);
        expect(groups[2].values.length).toEqual(2);

        expect(groups[0].values[0].name).toEqual('a');
        expect(groups[0].values[1].name).toEqual('a');
        expect(groups[0].values[2].name).toEqual('a');
        expect(groups[0].values[0].val).toEqual('a1');
        expect(groups[0].values[1].val).toEqual('a2');
        expect(groups[0].values[2].val).toEqual('a3');

        expect(groups[1].values[0].name).toEqual('b');
        expect(groups[1].values[0].val).toEqual('b1');

        expect(groups[2].values[0].name).toEqual('c');
        expect(groups[2].values[1].name).toEqual('c');
        expect(groups[2].values[0].val).toEqual('c2');
        expect(groups[2].values[1].val).toEqual('c1');
    });

    it('should groupArray 6 into 3 group,stuff groups', function () {
        //arr -> [{group:'a', stuff:[{ name: 'a', val: 'a1' }, { name: 'a', val: 'a2' },  { name: 'a', val: 'a3' }]},
        //        {group:'b', stuff:[{ name: 'b', val: 'b1' }]},
        //        {group:'c', stuff:[{ name: 'c', val: 'c2' }, { name: 'c', val: 'c1' }]}]
        var arr = [
            { name: 'a', val: 'a1' },
            { name: 'b', val: 'b1' },
            { name: 'c', val: 'c2' },
            { name: 'c', val: 'c1' },
            { name: 'a', val: 'a2' },
            { name: 'a', val: 'a3' },
        ];
        var keyfn = function (o) { return o.name; };
        var groups = u.groupArray(arr, keyfn, 'group', 'stuff');
        expect(groups.length).toEqual(3);
        expect(groups[0].group).toEqual('a');
        expect(groups[1].group).toEqual('b');
        expect(groups[2].group).toEqual('c');
        expect(groups[0].stuff.length).toEqual(3);
        expect(groups[1].stuff.length).toEqual(1);
        expect(groups[2].stuff.length).toEqual(2);

        expect(groups[0].stuff[0].name).toEqual('a');
        expect(groups[0].stuff[1].name).toEqual('a');
        expect(groups[0].stuff[2].name).toEqual('a');
        expect(groups[0].stuff[0].val).toEqual('a1');
        expect(groups[0].stuff[1].val).toEqual('a2');
        expect(groups[0].stuff[2].val).toEqual('a3');

        expect(groups[1].stuff[0].name).toEqual('b');
        expect(groups[1].stuff[0].val).toEqual('b1');

        expect(groups[2].stuff[0].name).toEqual('c');
        expect(groups[2].stuff[1].name).toEqual('c');
        expect(groups[2].stuff[0].val).toEqual('c2');
        expect(groups[2].stuff[1].val).toEqual('c1');
    });

});


